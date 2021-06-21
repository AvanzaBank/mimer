package com.avanza.astrix.config;

public class DynamicEnumProperty<T extends Enum<T>> implements DynamicProperty<T> {
	private final ListenerSupport<DynamicPropertyListener<T>> listenerSupport = new ListenerSupport<>();
	private volatile T value;

	@Override
	public T getCurrentValue() {
		return value;
	}

	@Override
	public void setValue(T value) {
		this.value = value;
		listenerSupport.dispatchEvent(it -> it.propertyChanged(value));
	}

	@Override
	public String toString() {
		return String.valueOf(value);
	}

	@Override
	public void addListener(DynamicPropertyListener<T> listener) {
		listenerSupport.addListener(listener);
	}

	@Override
	public void removeListener(DynamicPropertyListener<T> listener) {
		listenerSupport.removeListener(listener);
	}
}
