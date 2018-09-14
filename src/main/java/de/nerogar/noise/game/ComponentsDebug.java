package de.nerogar.noise.game;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;

public class ComponentsDebug {

	public static String generateComponentDebugString(Collection<Component> components) {

		StringBuilder sb = new StringBuilder();

		for (Component component : components) {
			sb.append(component.getClass().getSimpleName()).append('\n');

			for (Field field : component.getClass().getDeclaredFields()) {
				sb.append("\t").append(field.getName()).append(": ");
				//if (!field.canAccess(component)) {
				if (!field.isAccessible()) {
					field.setAccessible(true);
				}
				try {
					Object object = field.get(component);
					if (object == null) {
						sb.append("null\n");
					} else if (object.getClass().isArray()) {
						sb.append('[');
						int length = Array.getLength(object);
						for (int i = 0; i < length - 1; i++) {
							sb.append(Array.get(object, i)).append(',');
						}
						if (length > 0) {
							sb.append(Array.get(object, length - 1));
						}
						sb.append("]\n");
					} else if (object instanceof Map) {
						sb.append('{');

						Map<?, ?> map = (Map) object;
						for (Map.Entry<?, ?> entry : map.entrySet()) {
							sb.append('(').append(entry.getKey()).append(':').append(entry.getValue()).append(')');
						}
						sb.append("}\n");
					} else {
						sb.append(object).append('\n');
					}
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			}

		}

		return sb.toString();

	}

}
