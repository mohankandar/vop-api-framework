# VOP :: Starter Identity Client

Lightweight identity resolution from JWT claims or via HTTP service.

## Classes
- `VopIdentity` (record)
- `VopIdentityClient` (interface)
- `JwtClaimsIdentityClient` (claims)
- `HttpIdentityClient` (HTTP)
- `VopIdentityAutoConfiguration`

## Example
```yaml
vop:
  identity:
    http-enabled: true
    base-url: https://id.company/api
    path-template: /identity/{networkId}
    bearer: ${IDENTITY_SVC_TOKEN:}
```