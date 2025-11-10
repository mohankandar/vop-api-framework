# VOP :: BOM

This Bill of Materials (BOM) module defines all dependency versions used across the VOP framework.
It ensures consistent dependency management across modules without repeating version numbers.

## Purpose
- Centralize version management for Spring Boot and third‑party libraries.
- Simplify upgrades by changing versions in one place.

## Example Usage
```xml
<dependencyManagement>
  <dependencies>
    <dependency>
      <groupId>com.tnl.vop</groupId>
      <artifactId>vop-bom</artifactId>
      <version>0.1.0-SNAPSHOT</version>
      <type>pom</type>
      <scope>import</scope>
    </dependency>
  </dependencies>
</dependencyManagement>
```