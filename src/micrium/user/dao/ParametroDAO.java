package micrium.user.dao;

import java.io.Serializable;
import java.util.List;

import javax.annotation.Resource;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.UserTransaction;

import micrium.user.model.Parametro;

@Named
public class ParametroDAO implements Serializable {

	private static final long serialVersionUID = 1L;

	@PersistenceContext(unitName = "pUnit_dbSystem")
	private transient EntityManager entityManager;

	@Resource
	private transient UserTransaction transaction;

	public Parametro get(int id) throws Exception {
		return entityManager.find(Parametro.class, id);
	}

	public void update(Parametro dato) throws Exception {
		transaction.begin();
		entityManager.merge(dato);
		transaction.commit();
	}

	@SuppressWarnings("unchecked")
	public List<Parametro> getList() throws Exception {
		return entityManager.createQuery("SELECT pa FROM Parametro pa Order by pa.parametroId").getResultList();
	}


}
