# VOP :: Logging Config

Enterprise JSON logging with masking.

## Features
- `MaskingTurboFilter` scrubs SSN/CCN/tokens in messages & args
- Logback JSON via logstash‑logback‑encoder
- Templates: `logback-spring.xml`, `logback.xml`

## Use
Add dependency; avoid custom logback unless overriding.