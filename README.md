# XRABLE — Application-Level Standard for XR & Wearable Devices

## 1️⃣ Definition of XRABLE Standard

**What XRABLE is**

XRABLE is an application-level behavioral standard for XR and wearable systems. It defines the contracts by which applications discover capabilities, establish sessions, consume context, issue input/output intents, and respond to lifecycle events across heterogeneous body-worn devices.

**What class of problems it solves**

XRABLE solves interoperability and longevity problems for wearable and XR applications. It enables applications to remain functional across device generations, form factors, and vendors by standardizing capability discovery, lifecycle state, error behavior, and extension practices.

**What types of wearable/XR devices it targets**

XRABLE targets head-worn XR devices (AR/MR headsets and glasses), body-worn compute (watches, bracelets, rings), industrial wearables, and sensor-based body-worn devices that expose application-level interaction or context.

**What abstraction level it operates on**

XRABLE operates above hardware, firmware, and OS services, and below application logic. It standardizes *behavioral contracts* and *capability schemas*, not implementation details.

**What it explicitly does NOT cover**

XRABLE does not define hardware protocols, drivers, firmware, operating systems, rendering engines, UI frameworks, networking stacks, or any programming language bindings.

---

## 2️⃣ XRABLE in the Context of Wearable & XR Computing

Wearable and XR devices require a dedicated application-level standard because their hardware and interaction models evolve rapidly while application lifecycles are long. Traditional computing platforms assume stable form factors, persistent input devices, and static displays. In contrast, wearable systems are body-coupled, context-driven, and heavily sensor-dependent.

Unique challenges include:

- **Embodiment and context**: Applications are coupled to user posture, location, and physiological signals.
- **Latency sensitivity**: Perception and comfort require tight control over state transitions and responsiveness.
- **Transient availability**: Wearables are intermittently worn, powered, or connected.
- **Heterogeneous outputs**: Some devices are visual (glasses), others are haptic-only (rings), yet applications must adapt coherently.

XRABLE addresses these by defining a stable contract for state, capabilities, and events without prescribing any device implementation.

---

## 3️⃣ XRABLE Layer Model (OSI-like)

XRABLE defines an abstraction layer within a four-layer conceptual model.

1. **Physical/Device Layer (out of scope)**
   - Sensors, displays, actuators, radios, and power systems.
2. **Firmware/OS Layer (out of scope)**
   - Device drivers, firmware logic, scheduling, and OS services.
3. **XRABLE Core Layer (this standard)**
   - Application-facing behavioral contracts: capability model, lifecycle, events, error semantics, and domain contracts.
4. **Application Layer (out of scope)**
   - Product logic, UX decisions, and domain-specific application behavior.

XRABLE defines only the Core Layer boundaries and the contracts that applications can rely on regardless of lower-layer variability.

---

## 4️⃣ Core Concepts (Formal Definitions)

- **Wearable Device**: A body-worn computing system that exposes application-facing capabilities and context derived from sensors or actuators.
- **XR Device**: A wearable device that provides spatially-aware visual augmentation or spatial input aligned to a user’s physical environment.
- **Session**: A bounded, stateful interaction between an application and a device under XRABLE, with defined lifecycle states and guarantees.
- **Capability**: A discrete, versioned, discoverable unit of functionality exposed by a device, expressed via XRABLE contracts.
- **Provider**: The logical entity that implements a capability and emits events or fulfills requests under XRABLE.
- **Context**: Time-bound, device-provided information about the user, environment, or device state delivered via XRABLE events.
- **Event**: A structured, timestamped emission from a provider describing a state change, input, or context update.
- **State**: A named lifecycle condition of a session or capability that defines allowed operations and guarantees.
- **Error**: A standardized failure outcome emitted by XRABLE when a contract cannot be satisfied.

---

## 5️⃣ Lifecycle State Machine (MANDATORY)

XRABLE defines a formal lifecycle for devices and sessions. Each session must be in exactly one state at a time.

**States**

- **Disconnected**: No device association; no capabilities available.
- **Initializing**: Device is present; core negotiation and capability discovery are in progress.
- **Ready**: Capabilities are enumerated; no active data flows.
- **Active**: One or more capabilities are actively delivering data or rendering output.
- **Suspended**: The device remains associated but has halted data delivery due to user removal, power save, or environmental constraints.
- **Error**: A failure prevents normal operation; recovery may be possible.
- **Terminated**: Session is closed; no further operations are allowed.

**Allowed transitions**

- Disconnected → Initializing → Ready
- Ready → Active
- Active → Suspended → Active
- Ready/Active/Suspended → Error
- Error → Ready (if recoverable)
- Any state → Terminated
- Any state → Disconnected (if device is lost)

**State guarantees**

- **Initializing**: providers may be enumerated, but no operational guarantees are provided.
- **Ready**: capability schemas are stable, and all listed capabilities are available for activation.
- **Active**: providers must deliver events or outputs within domain-defined contracts.
- **Suspended**: providers may stop emitting events, but must preserve session identity and allow resumption.
- **Error**: providers must emit an error category and indicate recoverability.
- **Terminated**: no operations are valid; re-entry requires a new session.

**Behavior during disruptions**

- **Sensor loss**: Active → Suspended or Error depending on recoverability.
- **User removal**: Active → Suspended, with explicit removal reason.
- **Power events**: Active → Suspended for power-saving; Active → Terminated on forced shutdown.

---

## 6️⃣ Capability Model for Wearable & XR Devices

XRABLE models device functionality as discrete, discoverable capabilities. Capabilities are versioned, optional, and form-factor independent.

**Key properties**

- Capabilities are described with identifiers and version ranges.
- A device may implement multiple capabilities across domains.
- Capabilities may be activated or deactivated during a session.

**AR-specific capabilities**

Spatial display is represented as a capability with properties such as:

- **Display Mode** (head-locked, world-locked, body-locked)
- **Spatial Reference** (environmental frame validity)
- **Visual Channel** (mono/stereo, field-of-view range, opacity)

**Non-visual wearables**

Devices without displays (rings, bands) express capabilities in the same model, such as:

- **Haptic Output**
- **Gesture Input**
- **Physiological Context**

**Versioning and optionality**

Capabilities must be additive across versions. Devices may omit capabilities entirely without violating XRABLE compliance. Applications must use capability discovery before reliance.

---

## 7️⃣ XRABLE Core Domains (v0.1)

XRABLE defines exactly four core domains.

### A. Visual Augmentation (XR/AR)

**Responsibilities**
- Provide structured access to spatial display capabilities.
- Express spatial anchoring, rendering intent, and visual session readiness.

**Guarantees**
- Spatial reference validity is explicitly stated.
- Display mode is unambiguous.

**Lifecycle interaction**
- Active state is required to emit visual frames.

**Failure semantics**
- Loss of tracking transitions to Suspended or Error.

### B. Human Input

**Responsibilities**
- Model user intent inputs such as gestures, gaze, and device controls.

**Guarantees**
- Input events are timestamped and bound to a session.

**Lifecycle interaction**
- Inputs only emitted in Active state.

**Failure semantics**
- Input degradation must be surfaced as a recoverable error or reduced capability.

### C. Context & Sensors

**Responsibilities**
- Provide device and environmental context: motion, location, biometric, and environment signals.

**Guarantees**
- Context data includes temporal validity and confidence metadata.

**Lifecycle interaction**
- Context may be emitted in Ready or Active based on capability declarations.

**Failure semantics**
- Sensor invalidity must be emitted as explicit context loss.

### D. Connectivity & Session Control

**Responsibilities**
- Provide session establishment, capability negotiation, and state transitions.

**Guarantees**
- Lifecycle state transitions are explicit and monotonic.

**Lifecycle interaction**
- Governs all transitions and error state entry.

**Failure semantics**
- Connectivity loss triggers Disconnected or Error transitions.

---

## 8️⃣ Error and Recovery Model

**Error categories**

- **Capability Error**: a provider cannot satisfy its contract.
- **Context Error**: invalid or unavailable contextual data.
- **Session Error**: lifecycle or negotiation failure.
- **Resource Error**: insufficient compute, power, or bandwidth.

**Recoverable vs non-recoverable**

- Recoverable errors permit a transition from Error to Ready or Active.
- Non-recoverable errors require Terminated or Disconnected.

**Expected application behavior**

Applications must treat errors as explicit state transitions and must not assume silent recovery. Re-discovery of capabilities is required after any Error state.

**Relationship to lifecycle**

Errors are expressed as transitions into Error state with a category and recovery indicator. Recovery, if possible, must be explicit and results in a defined target state.

---

## 9️⃣ Versioning, Compatibility & Evolution Rules

XRABLE follows strict additive evolution:

- **Semantic versioning**: MAJOR.MINOR.PATCH.
- **MAJOR**: only when breaking the XRABLE Core Layer contract.
- **MINOR**: additive capabilities, domains, or constraints; no breaking changes.
- **PATCH**: clarifications or corrections; no contract change.

**Backward compatibility rules**

- A compliant implementation must support all required contracts of its declared XRABLE version.
- Applications must rely only on declared capability versions and optionality.
- Deprecated capabilities remain functional for at least one major version cycle.

**Adding new form factors**

New form factors are introduced only through new capabilities or extensions, never by altering existing contracts.

**Avoiding fragmentation**

- Extensions must be namespaced.
- Interoperability profiles may be defined to group capabilities, but they must be additive.

---

## 🔟 Extension & Vendor Integration Model

XRABLE allows vendor innovation without breaking interoperability.

**Namespace rules**

- Vendor extensions must use a reverse-domain or UUID namespace (e.g., `com.vendor.capability`).
- Standardized capabilities use the `xrable.*` namespace.

**Experimental vs stable extensions**

- Experimental extensions must be explicitly marked and may change without compatibility guarantees.
- Stable extensions must follow additive evolution rules.

**Promotion path**

- An extension may be proposed for standardization through a published compatibility profile.
- Once standardized, the capability is migrated into the `xrable.*` namespace with a versioned compatibility mapping.

---

## 1️⃣1️⃣ Conformance & Compliance

**XRABLE-compliant implementation**

A device or runtime is XRABLE-compliant if it:

- Implements the XRABLE Core Layer lifecycle and error semantics.
- Supports capability discovery and version negotiation.
- Implements at least one core domain.

**Minimal conformance requirements**

- Connectivity & Session Control is mandatory.
- At least one additional core domain must be implemented.

**Role of reference implementations**

Reference implementations may exist to validate the standard but are not required by the specification.

**Certification and testing**

A conformance test suite should validate lifecycle transitions, error handling, and capability negotiation across required domains.

---

## 1️⃣2️⃣ Long-Term Vision

XRABLE is designed to remain stable under rapid hardware change. It accommodates future display technologies, neural interfaces, and new interaction paradigms by extending the capability model rather than altering core contracts. XRABLE’s longevity depends on strict additive evolution, explicit state semantics, and a minimal core that allows independent implementations to remain interoperable over decades.
