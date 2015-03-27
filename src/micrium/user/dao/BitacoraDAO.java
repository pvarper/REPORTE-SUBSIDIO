package micrium.user.dao;

import java.io.Serializable;
import java.util.List;

import javax.annotation.Resource;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.UserTransaction;

import micrium.user.model.MuBitacora;

@Named
public class BitacoraDAO implements Serializable {

	private static final long serialVersionUID = 1L;

	@PersistenceContext(unitName = "pUnit_dbSystem")
	private transient EntityManager entityManager;

	@Resource
	private transient UserTransaction transaction;

	public void save(MuBitacora dato) throws Exception {
		transaction.begin();
		entityManager.persist(dato);
		transaction.commit();
	}

	@SuppressWarnings("unchecked")
	public List<MuBitacora> listBitacora() {
		String sql = "FROM MuBitacora b ORDER BY b.fecha DESC";
		Query query = entityManager.createQuery(sql);
		query.setMaxResults(1000);
		return query.getResultList();
	}

}
