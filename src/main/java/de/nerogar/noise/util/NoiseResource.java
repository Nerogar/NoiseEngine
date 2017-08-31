package de.nerogar.noise.util;

import de.nerogar.noise.Noise;

import java.lang.ref.PhantomReference;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class NoiseResource {

	private boolean cleaned = false;
	private PhantomReference<NoiseResource> reference;

	public NoiseResource() {
		add(this);
	}

	public boolean isCleaned() {
		return cleaned;
	}

	public abstract String getCleanupError();

	/**
	 * cleans all resources allocated by this object
	 *
	 * @return false, if this object is already cleaned, true otherwise
	 */
	public boolean cleanup() {
		if (cleaned) {
			return false;
		} else {
			cleaned = true;
			remove(this);
			return true;
		}
	}

	// static resource list

	private static Map<Reference<? extends NoiseResource>, String> referenceMap   = new ConcurrentHashMap<>();
	private static ReferenceQueue<NoiseResource>                   referenceQueue = new ReferenceQueue<>();

	private static void add(NoiseResource resource) {
		PhantomReference<NoiseResource> reference = new PhantomReference<>(resource, referenceQueue);
		referenceMap.put(reference, resource.getCleanupError());
		resource.reference = reference;
	}

	private static void remove(NoiseResource resource) {
		referenceMap.remove(resource.reference);
	}

	private static void processDelete() {
		while (true) {
			try {
				Reference<? extends NoiseResource> reference = referenceQueue.remove();

				if (referenceMap.containsKey(reference)) {
					Noise.getLogger().log(Logger.WARNING, referenceMap.remove(reference));
				}
			} catch (InterruptedException e) {
				// e.printStackTrace();
			}
		}
	}

	static {
		Thread thread = new Thread(NoiseResource::processDelete);
		thread.setName("resource warning");
		thread.setDaemon(true);
		thread.start();
	}

}
