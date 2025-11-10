# VOP :: Parent

Parent POM for the VOP framework. Provides shared build config, plugins, and test profiles.

## Key Features
- Java 21, UTF‑8, reproducible builds.
- Inherits versions from `vop-bom`.
- Common plugins (compiler, surefire, failsafe, jar, etc.).

## Example
```xml
<parent>
  <groupId>com.tnl.vop</groupId>
  <artifactId>vop-parent</artifactId>
  <version>0.1.0-SNAPSHOT</version>
</parent>
```