package com.tigo.dao;

import java.util.List;

public interface MasterDaoInterfaceBl {

	public String validar(Object entidad, boolean nuevo);

	public void save(Object entidad) throws Exception;

	public void remove(Object entidad) throws Exception;

	public void update(Object entidad) throws Exception;

	@SuppressWarnings({ "rawtypes" })
	public Object find(Object entityKey, Class clase) throws Exception;

	@SuppressWarnings("rawtypes")
	public List findAll() throws Exception;

}
