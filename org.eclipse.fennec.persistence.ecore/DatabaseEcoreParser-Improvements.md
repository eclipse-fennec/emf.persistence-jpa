# DatabaseEcoreParser - Improvements and Missing Features

This document outlines the current limitations and proposed improvements for the `DatabaseEcoreParser` component in `org.eclipse.fennec.persistence.ecore`.

## Overview

The `DatabaseEcoreParser` provides database reverse engineering capabilities to generate EMF Ecore models from existing database schemas. While the foundation is solid, several key areas need enhancement to make it production-ready for complex database environments.

## Current State

**Strengths:**
- ✅ Basic table to EClass mapping
- ✅ Column to EAttribute mapping
- ✅ Foreign key to EReference mapping
- ✅ OSGi component with configuration support
- ✅ Primary key detection
- ✅ DataSource integration

**Current Limitations:**
- ❌ Limited type conversion coverage
- ❌ PostgreSQL-only type names
- ❌ Simple relationship mapping only
- ❌ No naming convention transformations
- ❌ Missing constraint information
- ❌ Single schema support only

## Priority 1: High Impact Improvements

### 1. Comprehensive Type Mapping

**Current Issue:**
```java
static EDataType convertType(String colType) {
    if (colType.equals("int4")) return EcorePackage.Literals.EINTEGER_OBJECT;
    // ... only ~8 types supported
    return EcorePackage.Literals.ESTRING; // Everything else becomes String!
}
```

**Missing Types:**
- **Temporal Types**: `DATE`, `TIME`, `TIMESTAMP`, `DATETIME`, `INTERVAL`
- **Numeric Types**: `DECIMAL`, `NUMERIC`, `BIGINT`, `SMALLINT`, `REAL`, `DOUBLE`, `FLOAT`
- **Binary Types**: `BLOB`, `CLOB`, `BYTEA`, `VARBINARY`, `LONGVARBINARY`
- **Modern Types**: `JSON`, `JSONB`, `XML`, `UUID`
- **Array Types**: Database array types
- **Custom Types**: User-defined types, ENUMs

**Proposed Solution:**
```java
// Database vendor-specific type mapping
interface TypeMapper {
    EDataType mapType(String sqlType, int precision, int scale);
}

class PostgreSQLTypeMapper implements TypeMapper { ... }
class MySQLTypeMapper implements TypeMapper { ... }
class OracleTypeMapper implements TypeMapper { ... }
```

### 2. Multi-Database Vendor Support

**Current Problem:** Hardcoded PostgreSQL type names (`int4`, `float8`, `bpchar`)

**Missing Vendor Support:**
- **MySQL**: `TINYINT`, `MEDIUMINT`, `LONGTEXT`, `ENUM`
- **Oracle**: `NUMBER`, `VARCHAR2`, `CLOB`, `TIMESTAMP WITH TIME ZONE`
- **SQL Server**: `NVARCHAR`, `UNIQUEIDENTIFIER`, `DATETIME2`
- **H2/Derby**: For testing and embedded scenarios
- **SQLite**: For lightweight applications

**Implementation:**
- Vendor detection from JDBC metadata
- Pluggable type mapping strategies
- Database-specific naming conventions

### 3. Advanced Relationship Mapping

**Current Limitation:** Only handles simple foreign keys

**Missing Relationship Types:**

#### Many-to-Many Relationships
```sql
-- Junction table pattern not recognized
CREATE TABLE USER_ROLES (
    user_id INT REFERENCES users(id),
    role_id INT REFERENCES roles(id),
    PRIMARY KEY (user_id, role_id)
);
```

#### Composite Keys
```sql
-- Multi-column keys not handled
CREATE TABLE ORDER_ITEMS (
    order_id INT,
    item_seq INT,
    product_id INT,
    PRIMARY KEY (order_id, item_seq)
);
```

#### Self-References
```sql
-- Hierarchical structures
CREATE TABLE EMPLOYEES (
    id INT PRIMARY KEY,
    manager_id INT REFERENCES employees(id)
);
```

**Proposed Features:**
- Junction table detection and M:N mapping
- Composite key support
- Bidirectional reference creation
- Containment vs. reference distinction
- Self-reference handling

### 4. Naming Convention Transformation

**Current Problem:** Direct database names used as-is

**Examples of Needed Transformations:**
```java
// Database name → Java/EMF name
"USER_ACCOUNT"     → "UserAccount"     (snake_case to PascalCase)
"USERS"            → "User"            (plural to singular)
"TBL_CUSTOMER"     → "Customer"        (prefix removal)
"customer_orders"  → "customerOrders"  (camelCase for attributes)
"ORDER"            → "Order_"          (reserved word handling)
```

**Implementation Requirements:**
- Configurable naming strategies
- Pluralization/singularization rules
- Reserved word detection and handling
- Custom transformation patterns

## Priority 2: Essential Metadata Capture

### 5. Database Constraints

**Missing Constraint Information:**
- **Nullability**: `NOT NULL` constraints
- **Uniqueness**: `UNIQUE` constraints  
- **Check Constraints**: `CHECK` validations
- **Default Values**: Column defaults
- **String Lengths**: `VARCHAR(255)` size limits
- **Numeric Precision**: `DECIMAL(10,2)` precision/scale

**ECore Integration:**
```java
// Enhanced attribute creation
static void addAttribute(EClass eClass, String name, EClassifier type, 
                        boolean isId, boolean isRequired, boolean isUnique, 
                        String defaultValue, int length) {
    EAttribute attr = EcoreFactory.eINSTANCE.createEAttribute();
    attr.setRequired(isRequired);
    attr.setUnique(isUnique);
    // Set bounds, default values, etc.
}
```

### 6. Multi-Schema Support

**Current Limitation:** Only processes single schema (`PUBLIC` default)

**Needed Enhancements:**
- Process multiple schemas in one database
- Cross-schema foreign key handling
- Schema-based EPackage organization
- Namespace collision resolution

**Configuration:**
```java
@interface DatabaseParserConfig {
    String[] schemas() default {"PUBLIC"};
    String packagePerSchema() default "true";
    String schemaPrefix() default "";
}
```

## Priority 3: Advanced Features

### 7. Database Views Support

**Current Gap:** Views are ignored entirely

**View Handling Needs:**
- Detect database views
- Generate read-only EClasses
- Handle view dependencies
- Support materialized views

### 8. EPackage Organization

**Current Problem:** All tables in single EPackage

**Improvement Options:**
- Schema-based package organization
- Size-based package splitting
- Domain-based grouping (configurable)
- Dependency-aware organization

### 9. Incremental Updates

**Missing Capability:** Cannot update existing models

**Needed Features:**
- Compare database vs. existing ECore model
- Generate change reports
- Update existing models preserving customizations
- Migration script generation

### 10. Custom Type Handling

**Advanced Type Support:**
- Database ENUM types → EMF Enumerations
- User-defined types (UDTs)
- Spatial/Geographic types (PostGIS, etc.)
- Domain-specific types

## Implementation Roadmap

### Phase 1: Foundation (High Priority)
1. **Vendor-Agnostic Type System**
   - Abstract type mapping interface
   - PostgreSQL, MySQL, Oracle, H2 mappers
   - Comprehensive type coverage

2. **Naming Convention Engine**
   - Configurable transformation rules
   - Built-in conventions (snake_case, camelCase, etc.)
   - Reserved word handling

3. **Constraint Metadata Capture**
   - NOT NULL, UNIQUE constraints
   - String lengths and numeric precision
   - Default values

### Phase 2: Relationships (Medium Priority)
4. **Advanced Relationship Detection**
   - Many-to-many junction tables
   - Composite key support
   - Bidirectional references

5. **Multi-Schema Support**
   - Multiple schema processing
   - Cross-schema references
   - Package organization strategies

### Phase 3: Advanced Features (Lower Priority)
6. **View Support**
7. **Incremental Updates**
8. **Custom Type Extensions**
9. **Performance Optimization**

## Configuration Enhancement

**Enhanced Configuration Interface:**
```java
@ObjectClassDefinition
interface DatabaseParserConfig {
    // Basic settings
    String packageName();
    String uriPrefix();
    String version() default "1.0";
    
    // New settings
    String[] schemas() default {"PUBLIC"};
    String namingConvention() default "SNAKE_TO_CAMEL";
    String vendor() default "AUTO_DETECT";
    boolean createBidirectionalReferences() default true;
    boolean detectManyToMany() default true;
    boolean captureConstraints() default true;
    String packageOrganization() default "SINGLE"; // SINGLE, BY_SCHEMA, BY_SIZE
}
```

## Testing Strategy

**Comprehensive Test Coverage Needed:**
- Multiple database vendor tests
- Complex schema scenarios
- Large schema performance tests
- Edge case handling (empty schemas, circular refs, etc.)
- Migration/update scenarios

## Conclusion

The `DatabaseEcoreParser` has excellent potential but needs these enhancements to become a robust, production-ready tool for database reverse engineering. The priority should be on type mapping, naming conventions, and constraint capture, as these provide the highest impact for users.