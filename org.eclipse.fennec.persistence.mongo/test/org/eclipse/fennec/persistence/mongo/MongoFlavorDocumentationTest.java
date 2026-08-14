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
package org.eclipse.fennec.persistence.mongo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.fennec.persistence.capabilities.QueryFeature;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Keeps the documented flavor capability matrix in sync with the code (issue #120).
 * <p>
 * A hand-maintained capability table is wrong within two releases, and a wrong one is worse
 * than none: users choose a server and plan queries against it. The guides therefore mark
 * their gap lists machine-readably and this test compares them with
 * {@link MongoFlavorCapabilities}.
 *
 * @author Mark Hoffmann
 * @since 09.08.2026
 */
class MongoFlavorDocumentationTest {

	/**
	 * The guide carrying the matrix. Only the {@code docs/} original is checked:
	 * {@code docs-site/docs/guides/} is generated from it by {@code sync-guides.mjs} and is
	 * git-ignored, so it does not exist in a fresh checkout.
	 */
	private static final List<String> GUIDES = List.of("docs/mongo-user-guide.md");

	private static final Pattern GAP_BLOCK = Pattern.compile(
			"<!--\\s*flavor-gaps:([a-z0-9-]+)\\s*-->(.*?)<!--\\s*/flavor-gaps\\s*-->", Pattern.DOTALL);

	@Test
	@DisplayName("every guide documents each flavor's gaps exactly as the code declares them")
	void documentedGapsMatchTheCode() throws IOException {
		for (String guide : GUIDES) {
			Path path = locate(guide);
			String content = Files.readString(path);
			Matcher matcher = GAP_BLOCK.matcher(content);

			List<MongoFlavor> documented = new ArrayList<>();
			while (matcher.find()) {
				MongoFlavor flavor = MongoFlavor.byId(matcher.group(1)).orElseGet(
						() -> fail("%s documents gaps for unknown flavor '%s'", guide, matcher.group(1)));
				documented.add(flavor);

				assertThat(featuresIn(matcher.group(2)))
						.as("%s documents the gaps of flavor '%s'", guide, flavor.id())
						.isEqualTo(MongoFlavorCapabilities.gapsOf(flavor));
			}

			// MONGO is the baseline and has no gap block by definition
			assertThat(documented).as("flavors documented in %s", guide)
					.containsExactlyInAnyOrder(MongoFlavor.FERRETDB, MongoFlavor.DOCUMENTDB_PG);
		}
	}

	/**
	 * The features named in a documentation block. An empty result means "no gaps" — the
	 * prose says so in words, which is why only recognised feature names count, not the
	 * presence of text.
	 */
	private static Set<QueryFeature> featuresIn(String block) {
		Set<QueryFeature> features = EnumSet.noneOf(QueryFeature.class);
		for (QueryFeature feature : QueryFeature.values()) {
			if (Pattern.compile("\\b" + Pattern.quote(feature.getName()) + "\\b").matcher(block).find()) {
				features.add(feature);
			}
		}
		return features;
	}

	/**
	 * Resolves a repository-relative path from the module working directory. Failing loudly
	 * rather than skipping: a silent skip would let the matrix rot precisely when nobody is
	 * watching.
	 */
	private static Path locate(String repositoryRelative) {
		Path directory = Path.of("").toAbsolutePath();
		for (int level = 0; level < 4 && directory != null; level++) {
			Path candidate = directory.resolve(repositoryRelative);
			if (Files.isRegularFile(candidate)) {
				return candidate;
			}
			directory = directory.getParent();
		}
		return fail("Could not locate '%s' from %s — the flavor matrix cannot be verified",
				repositoryRelative, Path.of("").toAbsolutePath());
	}
}
