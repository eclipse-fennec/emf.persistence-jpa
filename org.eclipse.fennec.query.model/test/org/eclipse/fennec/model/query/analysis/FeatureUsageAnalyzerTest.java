/********************************************************************
 * Copyright (c) 2026 Contributors to the Eclipse Foundation.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *   Data In Motion Consulting - initial implementation
 ********************************************************************/
package org.eclipse.fennec.model.query.analysis;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.eclipse.fennec.model.query.builder.Expressions.add;
import static org.eclipse.fennec.model.query.builder.Expressions.aliasRef;
import static org.eclipse.fennec.model.query.builder.Expressions.and;
import static org.eclipse.fennec.model.query.builder.Expressions.any;
import static org.eclipse.fennec.model.query.builder.Expressions.count;
import static org.eclipse.fennec.model.query.builder.Expressions.geoBox;
import static org.eclipse.fennec.model.query.builder.Expressions.geoPoint;
import static org.eclipse.fennec.model.query.builder.Expressions.geoSubjectLatLon;
import static org.eclipse.fennec.model.query.builder.Expressions.geoWithin;
import static org.eclipse.fennec.model.query.builder.Expressions.isOf;
import static org.eclipse.fennec.model.query.builder.Expressions.mapValue;
import static org.eclipse.fennec.model.query.builder.Expressions.path;
import static org.eclipse.fennec.model.query.builder.Expressions.pathAs;
import static org.eclipse.fennec.model.query.builder.Expressions.propertyPath;
import static org.eclipse.fennec.model.query.builder.Expressions.rootReference;

import java.util.List;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.fennec.model.expression.Expression;
import org.eclipse.fennec.model.expression.ExpressionPackage;
import org.eclipse.fennec.model.query.Query;
import org.eclipse.fennec.model.query.QueryPackage;
import org.eclipse.fennec.model.query.builder.Expands;
import org.eclipse.fennec.model.query.builder.QueryBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests the {@link FeatureUsageAnalyzer}: the role per query slot, the implicit delivery of
 * whole objects and the completeness of the slot mapping (issue #292).
 *
 * @author Mark Hoffmann
 */
class FeatureUsageAnalyzerTest {

	private final EcoreFactory ecore = EcoreFactory.eINSTANCE;

	private EClass person;
	private EAttribute name;
	private EAttribute age;
	private EAttribute email;
	private EReference addresses;
	private EReference employer;
	private EReference attributes;
	private EClass address;
	private EAttribute street;
	private EAttribute lat;
	private EAttribute lon;
	private EClass company;
	private EAttribute companyName;
	private EAttribute taxId;
	private EClass employee;
	private EAttribute salary;
	private EClass entry;

	@BeforeEach
	void setUp() {
		address = eClass("Address");
		street = attribute(address, "street", EcorePackage.Literals.ESTRING);
		lat = attribute(address, "lat", EcorePackage.Literals.EDOUBLE);
		lon = attribute(address, "lon", EcorePackage.Literals.EDOUBLE);

		company = eClass("Company");
		companyName = attribute(company, "name", EcorePackage.Literals.ESTRING);
		taxId = attribute(company, "taxId", EcorePackage.Literals.ESTRING);

		entry = eClass("StringToStringMapEntry");
		entry.setInstanceClassName("java.util.Map$Entry");
		attribute(entry, "key", EcorePackage.Literals.ESTRING);
		attribute(entry, "value", EcorePackage.Literals.ESTRING);

		person = eClass("Person");
		name = attribute(person, "name", EcorePackage.Literals.ESTRING);
		age = attribute(person, "age", EcorePackage.Literals.EINT);
		email = attribute(person, "email", EcorePackage.Literals.ESTRING);
		addresses = reference(person, "addresses", address, true);
		employer = reference(person, "employer", company, false);
		attributes = reference(person, "attributes", entry, true);

		employee = eClass("Employee");
		employee.getESuperTypes().add(person);
		salary = attribute(employee, "salary", EcorePackage.Literals.EINT);
	}

	/**
	 * The trap the analysis exists for: an OBJECTS query names one feature and hands out all
	 * of them. The containment closure travels with the object, a non-containment target
	 * does not — it arrives as a proxy, only the reference itself is delivered.
	 */
	@Test
	void objectsQueryDeliversTheWholeRootWithItsContainments() {
		FeatureUsage usage = FeatureUsageAnalyzer.analyze(QueryBuilder.from(person)
				.where(path(age).gt(18)).build());

		assertThat(usage.from()).isSameAs(person);
		assertThat(usage.deliversObjects()).isTrue();
		assertThat(usage.features()).containsExactly(age);
		assertThat(usage.features(FeatureRole.FILTER)).containsExactly(age);
		assertThat(usage.deliveredTypes()).containsExactlyInAnyOrder(person, address, entry);
		assertThat(usage.exposedFeatures()).contains(name, age, email, addresses, employer, street, lat)
				.doesNotContain(companyName, taxId);
	}

	@Test
	void projectionExposesOnlyWhatItProjects() {
		FeatureUsage usage = FeatureUsageAnalyzer.analyze(QueryBuilder.from(person)
				.where(path(email).eq("a@b.c")).select(name).build());

		assertThat(usage.deliversObjects()).isFalse();
		assertThat(usage.deliveredTypes()).isEmpty();
		assertThat(usage.features(FeatureRole.PROJECTION)).containsExactly(name);
		assertThat(usage.features(FeatureRole.FILTER)).containsExactly(email);
		assertThat(usage.exposedFeatures()).containsExactly(name);
	}

	@Test
	void countDeliversNothingButStillFilters() {
		FeatureUsage usage = FeatureUsageAnalyzer.analyze(QueryBuilder.from(person)
				.where(path(email).eq("a@b.c")).expand(employer).countOnly().build());

		assertThat(usage.deliversObjects()).isFalse();
		assertThat(usage.deliveredTypes()).isEmpty();
		assertThat(usage.exposedFeatures()).isEmpty();
		assertThat(usage.features(FeatureRole.FILTER)).containsExactly(email);
	}

	@Test
	void sortPathsAreNotExposed() {
		FeatureUsage usage = FeatureUsageAnalyzer.analyze(QueryBuilder.from(person)
				.select(name).orderByDesc(age).build());

		assertThat(usage.features(FeatureRole.SORT)).containsExactly(age);
		assertThat(usage.exposedFeatures()).containsExactly(name);
	}

	/**
	 * Quantifier, geo subject and map value are nested expression constructs — the climb to
	 * the slot covers them without a visitor per node type.
	 */
	@Test
	void nestedExpressionConstructsInheritTheSlotRole() {
		FeatureUsage usage = FeatureUsageAnalyzer.analyze(QueryBuilder.from(person)
				.where(and(
						any(propertyPath(addresses), it -> it.path(street).eq("Main St")),
						geoWithin(geoSubjectLatLon(propertyPath(addresses, lat), propertyPath(addresses, lon)),
								geoBox(geoPoint(10, 50), geoPoint(11, 51))),
						mapValue(attributes, "color").eq("red")))
				.select(name).build());

		assertThat(usage.features(FeatureRole.FILTER))
				.containsExactly(addresses, street, lat, lon, attributes);
		assertThat(usage.paths()).filteredOn(p -> p.role() == FeatureRole.FILTER)
				.extracting(PathUse::segments)
				.containsExactly(List.of(addresses), List.of(street), List.of(addresses, lat),
						List.of(addresses, lon), List.of(attributes));
		assertThat(usage.paths()).extracting(PathUse::feature).containsExactly(
				addresses, street, lat, lon, attributes, name);
	}

	/**
	 * A predicate inside a projected count shapes a delivered column, so its paths are
	 * projection — the role is the slot's, not the construct's.
	 */
	@Test
	void predicateInsideAProjectedCountIsProjection() {
		FeatureUsage usage = FeatureUsageAnalyzer.analyze(QueryBuilder.from(person)
				.selectAs("inMain", count(propertyPath(addresses), it -> it.path(street).eq("Main St"))
						.toExpression())
				.build());

		assertThat(usage.features(FeatureRole.PROJECTION)).containsExactly(addresses, street);
		assertThat(usage.features(FeatureRole.FILTER)).isEmpty();
		assertThat(usage.exposedFeatures()).containsExactly(addresses, street);
	}

	@Test
	void groupingAndAggregatesAreExposedButNoObject() {
		FeatureUsage usage = FeatureUsageAnalyzer.analyze(QueryBuilder.from(person)
				.groupBy(name).avg("avgAge", age).having(aliasRef("avgAge").gt(30)).build());

		assertThat(usage.deliversObjects()).isFalse();
		assertThat(usage.features(FeatureRole.GROUP)).containsExactly(name);
		assertThat(usage.features(FeatureRole.AGGREGATE)).containsExactly(age);
		assertThat(usage.exposedFeatures()).containsExactly(name, age);
	}

	/** Representatives put the grouped documents into a row cell — whole objects again. */
	@Test
	void representativesDeliverWholeObjects() {
		FeatureUsage usage = FeatureUsageAnalyzer.analyze(QueryBuilder.from(person)
				.groupBy(name).representatives("members", 3).build());

		assertThat(usage.deliversObjects()).isTrue();
		assertThat(usage.deliveredTypes()).contains(person, address);
		assertThat(usage.exposedFeatures()).contains(email);
	}

	@Test
	void computeIsExposedAndMakesTheResultRowShaped() {
		FeatureUsage usage = FeatureUsageAnalyzer.analyze(QueryBuilder.from(person)
				.computeAs("nextAge", add(path(age), 1).toExpression()).build());

		assertThat(usage.deliversObjects()).isFalse();
		assertThat(usage.features(FeatureRole.COMPUTE)).containsExactly(age);
		assertThat(usage.exposedFeatures()).containsExactly(age);
	}

	/**
	 * An expansion resolves the proxies, so the target type is delivered whole; its filter
	 * addresses the expanded type and is filter, not exposure.
	 */
	@Test
	void expansionDeliversItsTarget() {
		FeatureUsage usage = FeatureUsageAnalyzer.analyze(QueryBuilder.from(person)
				.expand(Expands.of(employer).filter(path(companyName).eq("ACME")).build()).build());

		assertThat(usage.features(FeatureRole.EXPAND)).containsExactly(employer);
		assertThat(usage.features(FeatureRole.FILTER)).containsExactly(companyName);
		assertThat(usage.deliveredTypes()).contains(person, company);
		assertThat(usage.exposedFeatures()).contains(companyName, taxId);
	}

	/** A projected reference hands out its target — counted conservatively as a whole object. */
	@Test
	void projectedReferenceDeliversItsTarget() {
		FeatureUsage usage = FeatureUsageAnalyzer.analyze(QueryBuilder.from(person)
				.select(employer).build());

		assertThat(usage.deliversObjects()).isFalse();
		assertThat(usage.deliveredTypes()).containsExactly(company);
		assertThat(usage.exposedFeatures()).containsExactlyInAnyOrder(employer, companyName, taxId);
	}

	@Test
	void namedTypesAreReported() {
		FeatureUsage usage = FeatureUsageAnalyzer.analyze(QueryBuilder.from(person)
				.where(and(
						pathAs(employee, salary).gt(1000),
						isOf(employee),
						path(employer, companyName).eq(
								rootReference(company, path(taxId).eq("DE1"), propertyPath(companyName)))))
				.build());

		assertThat(usage.types()).containsExactly(person, employee, company);
		assertThat(usage.features(FeatureRole.FILTER))
				.containsExactly(salary, employer, companyName, taxId);
	}

	/**
	 * The analysis knows the static type only: an Employee delivered by a Person query
	 * carries its salary, which the caller has to add by widening the delivered types.
	 */
	@Test
	void deliveredFeaturesFollowTheStaticTypeOnly() {
		FeatureUsage usage = FeatureUsageAnalyzer.analyze(QueryBuilder.from(person).build());

		assertThat(usage.deliveredTypes()).doesNotContain(employee);
		assertThat(usage.exposedFeatures()).doesNotContain(salary);
	}

	@Test
	void bareExpressionIsAllFilterAndDeliversNothing() {
		Expression selector = and(path(email).eq("a@b.c"), pathAs(employee, salary).gt(1));

		FeatureUsage usage = FeatureUsageAnalyzer.analyze(selector);

		assertThat(usage.from()).isNull();
		assertThat(usage.deliversObjects()).isFalse();
		assertThat(usage.deliveredTypes()).isEmpty();
		assertThat(usage.types()).containsExactly(employee);
		assertThat(usage.features(FeatureRole.FILTER)).containsExactly(email, salary);
	}

	@Test
	void bareExpressionStopsAtItsRootInsideAQuery() {
		Query query = QueryBuilder.from(person).select(name).orderByAsc(age).build();
		Expression sortKey = query.getOrderBy().get(0).getPath();

		FeatureUsage usage = FeatureUsageAnalyzer.analyze(sortKey);

		assertThat(usage.paths()).containsExactly(new PathUse(FeatureRole.FILTER, List.of(age)));
	}

	@Test
	void nullIsRejected() {
		assertThatIllegalArgumentException().isThrownBy(() -> FeatureUsageAnalyzer.analyze((Query) null));
		assertThatIllegalArgumentException()
				.isThrownBy(() -> FeatureUsageAnalyzer.analyze((Expression) null));
	}

	/**
	 * Every expression-valued slot of the query model has a role. A new slot fails here
	 * instead of having its paths reported under no role — or not at all.
	 */
	@Test
	void everyExpressionSlotOfTheQueryModelHasARole() {
		for (EClassifier classifier : QueryPackage.eINSTANCE.getEClassifiers()) {
			if (classifier instanceof EClass eClass) {
				for (EReference slot : eClass.getEReferences()) {
					if (slot.isContainment()
							&& slot.getEReferenceType().getEPackage() == ExpressionPackage.eINSTANCE) {
						assertThat(FeatureUsageAnalyzer.roleOf(slot))
								.as("role of %s.%s", eClass.getName(), slot.getName()).isNotNull();
					}
				}
			}
		}
	}

	private EClass eClass(String className) {
		EClass eClass = ecore.createEClass();
		eClass.setName(className);
		return eClass;
	}

	private EAttribute attribute(EClass owner, String attributeName, EClassifier type) {
		EAttribute attribute = ecore.createEAttribute();
		attribute.setName(attributeName);
		attribute.setEType(type);
		owner.getEStructuralFeatures().add(attribute);
		return attribute;
	}

	private EReference reference(EClass owner, String referenceName, EClass type, boolean containment) {
		EReference reference = ecore.createEReference();
		reference.setName(referenceName);
		reference.setEType(type);
		reference.setUpperBound(-1);
		reference.setContainment(containment);
		owner.getEStructuralFeatures().add(reference);
		return reference;
	}

}
