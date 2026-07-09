# Commonly used mixins and example mockups

## Inject

```java
@Inject(method = "doThing", at = @At("HEAD"))
private void injectedCodeAtMethodHead(CallbackInfo ci) {
    System.out.println("This will run at the start of the method");
}
```

Runs code at a specific point in an existing method. This is the most commonly used mixin type.

Common injection points:

* `HEAD`: runs at the start of the method
* `RETURN`: runs before the method returns
* `TAIL`: runs at the very end of the method
* specific bytecode locations using `@At`

---

## Redirect

```java
@Redirect(method = "doThing", at = @At(value = "INVOKE", target = "Lexample/TargetClass;targetMethod()V"))
private void redirectMethodCall(TargetClass instance) {
    System.out.println("This replaces the original method call");
}
```

Replaces a specific method call, field access, or object creation inside another method.

Example use cases:

* changing how an existing method behaves
* replacing vanilla logic with custom logic
* preventing a method from being called

The redirect method must have parameters matching the original operation being redirected.

Example:

Original code:

```java
entity.attackEntity(player);
```

Mixin:

```java
@Redirect(method = "updateEntity", at = @At(value = "INVOKE", target = "LEntity;attackEntity(LPlayer;)V"))
private void redirectAttack(Entity entity, Player player) {
    System.out.println("Custom attack handling");
}
```

---

## ModifyConstant

```java
@ModifyConstant(method = "doThing", constant = @Constant(intValue = 10))
private int modifyConstant(int original) {
    return 20;
}
```

Changes a constant value used inside a method.

Commonly used for:

* changing damage values
* changing timers
* modifying hardcoded limits
* changing probabilities

Examples:

```java
@ModifyConstant(method = "updateEntity", constant = @Constant(floatValue = 0.5F))
private float modifySpeedMultiplier(float original) {
    return 0.25F;
}
```

This changes every matching `0.5F` constant in the target method unless more restrictions are added.

---

## ModifyArg

```java
@ModifyArg(method = "doThing", at = @At(value = "INVOKE", target = "Lexample/TargetClass;targetMethod(I)V"))
private int modifyMethodArgument(int original) {
    return original * 2;
}
```

Changes a single argument passed into a method call.

Useful when:

* the method itself should stay unchanged
* only one parameter needs to be adjusted
* redirecting the entire method would be excessive

Example:

Original code:

```java
damageEntity(entity, 5);
```

Mixin:

```java
@ModifyArg(method = "attack", at = @At(value = "INVOKE", target = "LEntity;damageEntity(LivingEntity;I)V"))
private int increaseDamage(int damage) {
    return damage + 10;
}
```

The method still runs normally, but receives the modified argument.

---

## ModifyArgs

```java
@ModifyArgs(method = "doThing", at = @At(value = "INVOKE", target = "Lexample/TargetClass;targetMethod(II)V"))
private void modifyMultipleArguments(Args args) {
    args.set(0, 10);
    args.set(1, 20);
}
```

Similar to `ModifyArg`, but allows modifying multiple arguments at once.

Useful when a method call has several parameters that need changing.

---

## ModifyVariable

```java
@ModifyVariable(method = "doThing", at = @At("STORE"))
private int modifyLocalVariable(int value) {
    return value * 2;
}
```

Changes a local variable inside a method.

Useful for:

* changing calculations
* modifying temporary values
* altering internal logic without replacing the whole method

---

## Overwrite

```java
@Overwrite
public void doThing() {
    System.out.println("This completely replaces the original method");
}
```

Completely replaces an existing method.

Avoid using this unless absolutely necessary. It removes compatibility with other mods and can easily break future changes. Prefer `Inject`, `Redirect`, or other targeted mixins whenever possible.
