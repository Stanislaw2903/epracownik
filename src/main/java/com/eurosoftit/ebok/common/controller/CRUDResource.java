package com.eurosoftit.ebok.common.controller;

import java.io.Serializable;

public interface CRUDResource<K extends Serializable, E extends IEntity<K>, D extends DTO<K>, P> extends SearchResource<K, E, P, D>, DeleteResource<K, E, D>, UpdateResource<K, E, D>, SaveResource<K, E, D>{
}
