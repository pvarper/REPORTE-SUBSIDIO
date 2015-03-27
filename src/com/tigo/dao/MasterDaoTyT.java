package com.tigo.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.UserTransaction;

@Named
public class MasterDaoTyT implements Serializable {

	private static final long serialVersionUID = 1L;

	@PersistenceContext(unitName = "pUnit_dbSystemExtern")
	private transient EntityManager entityManager;

	@Resource
	private transient UserTransaction transaction;

	public void save(Object entidad) throws Exception {
		transaction.begin();
		entityManager.joinTransaction();
		try {
			entityManager.persist(entidad);
			transaction.commit();
		} catch (Exception e) {
			transaction.rollback();
			throw e;
		}
	}

	public void remove(Object entidad) throws Exception {
		transaction.begin();
		entityManager.joinTransaction();
		try {
			entityManager.remove(entityManager.contains(entidad) ? entidad : entityManager.merge(entidad));
			transaction.commit();
		} catch (Exception e) {
			transaction.rollback();
			throw e;
		}
	}

	public void update(Object entidad) throws Exception {
		transaction.begin();
		entityManager.joinTransaction();
		try {
			entityManager.merge(entidad);
			transaction.commit();
		} catch (Exception e) {
			transaction.rollback();
			throw e;
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Object find(Object entityKey, Class clase) throws Exception {
		try {
			return entityManager.find(clase, entityKey);
		} catch (Exception e) {
			throw e;
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List findAllQuery(Class clase, String sql, PQuery p) throws Exception {
		Map<String, Object> parametros = p != null ? p.getParametros() : null;
		try {
			Query query = entityManager.createQuery(sql, clase);
			if (parametros != null) {
				List<String> keys = new ArrayList<String>(parametros.keySet());
				for (String key : keys) {
					query.setParameter(key, parametros.get(key));
				}
			}
			return query.getResultList();
		} catch (Exception e) {
			throw e;
		}
	}

	@SuppressWarnings({ "rawtypes" })
	public List findAllNativeQuery(String sql, PQuery p) throws Exception {
		Map<String, Object> parametros = p != null ? p.getParametros() : null;
		try {
			Query query = entityManager.createNativeQuery(sql);
			if (parametros != null) {
				List<String> keys = new ArrayList<String>(parametros.keySet());
				for (String key : keys) {
					query.setParameter(key, parametros.get(key));
				}
			}
			return query.getResultList();
		} catch (Exception e) {
			throw e;
		}
	}

	@SuppressWarnings({ "rawtypes" })
	public List findAllNativeQuery(Class clase, String sql, PQuery p) throws Exception {
		Map<String, Object> parametros = p != null ? p.getParametros() : null;
		try {
			Query query = entityManager.createNativeQuery(sql, clase);
			if (parametros != null) {
				List<String> keys = new ArrayList<String>(parametros.keySet());
				for (String key : keys) {
					query.setParameter(key, parametros.get(key));
				}
			}
			return query.getResultList();
		} catch (Exception e) {
			throw e;
		}
	}

	public void executeUpdateNativeQuery(String sql, PQuery p) throws Exception {
		Map<String, Object> parametros = p != null ? p.getParametros() : null;
		transaction.begin();
		entityManager.joinTransaction();
		try {
			Query query = entityManager.createNativeQuery(sql);
			if (parametros != null) {
				List<String> keys = new ArrayList<String>(parametros.keySet());
				for (String key : keys) {
					query.setParameter(key, parametros.get(key));
				}
			}
			query.executeUpdate();
			transaction.commit();
		} catch (Exception e) {
			transaction.rollback();
			throw e;
		}
	}

}
