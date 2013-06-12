package com.twitter.mesos.scheduler.storage.mem;

import com.google.inject.Key;
import com.google.inject.PrivateModule;
import com.google.inject.Singleton;

import com.twitter.common.inject.Bindings.KeyFactory;
import com.twitter.mesos.scheduler.storage.AttributeStore;
import com.twitter.mesos.scheduler.storage.JobStore;
import com.twitter.mesos.scheduler.storage.QuotaStore;
import com.twitter.mesos.scheduler.storage.SchedulerStore;
import com.twitter.mesos.scheduler.storage.Storage;
import com.twitter.mesos.scheduler.storage.Storage.Volatile;
import com.twitter.mesos.scheduler.storage.TaskStore;
import com.twitter.mesos.scheduler.storage.UpdateStore;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Binding module for an in-memory storage system.
 * <p>
 * Exposes bindings for storage components:
 * <ul>
 *   <li>{@link com.twitter.mesos.scheduler.storage.Storage}</li>
 *   <li>Keyed with keys provided by the provided{@code keyFactory}:</li>
 *     <ul>
 *       <li>{@link com.twitter.mesos.scheduler.storage.SchedulerStore}</li>
 *       <li>{@link com.twitter.mesos.scheduler.storage.JobStore}</li>
 *       <li>{@link com.twitter.mesos.scheduler.storage.TaskStore}</li>
 *       <li>{@link com.twitter.mesos.scheduler.storage.UpdateStore}</li>
 *       <li>{@link com.twitter.mesos.scheduler.storage.QuotaStore}</li>
 *       <li>{@link com.twitter.mesos.scheduler.storage.AttributeStore}</li>
 *     </ul>
 * </ul>
 */
public final class MemStorageModule extends PrivateModule {

  private final KeyFactory keyFactory;

  public MemStorageModule(KeyFactory keyFactory) {
    this.keyFactory = checkNotNull(keyFactory);
  }

  private <T> void bindStore(Class<T> binding, Class<? extends T> impl) {
    bind(binding).to(impl);
    bind(impl).in(Singleton.class);
    Key<T> key = keyFactory.create(binding);
    bind(key).to(impl);
    expose(key);
  }

  @Override
  protected void configure() {
    Key<Storage> storageKey = keyFactory.create(Storage.class);
    bind(storageKey).to(MemStorage.class);
    expose(storageKey);
    Key<Storage> exposedMemStorageKey = Key.get(Storage.class, Volatile.class);
    bind(exposedMemStorageKey).to(MemStorage.class);
    expose(exposedMemStorageKey);
    bind(MemStorage.class).in(Singleton.class);

    bindStore(SchedulerStore.Mutable.class, MemSchedulerStore.class);
    bindStore(JobStore.Mutable.class, MemJobStore.class);
    bindStore(TaskStore.Mutable.class, MemTaskStore.class);
    bindStore(UpdateStore.Mutable.class, MemUpdateStore.class);
    bindStore(QuotaStore.Mutable.class, MemQuotaStore.class);
    bindStore(AttributeStore.Mutable.class, MemAttributeStore.class);
  }
}
