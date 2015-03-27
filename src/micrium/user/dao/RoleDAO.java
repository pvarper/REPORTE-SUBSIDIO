package micrium.user.dao;

import java.io.Serializable;
import java.util.List;

import javax.annotation.Resource;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.UserTransaction;

import micrium.user.model.MuRol;
import micrium.user.model.MuRolFormulario;

@Named
public class RoleDAO implements Serializable {

	private static final long serialVersionUID = 1L;

	@PersistenceContext(unitName = "pUnit_dbSystem")
	private transient EntityManager entityManager;

	@Resource
	private transient UserTransaction transaction;

	public void save(MuRol dato) throws Exception {
		transaction.begin();
		entityManager.persist(dato);
		transaction.commit();
	}

	public void saveRolForulario(MuRolFormulario dato) throws Exception {

		transaction.begin();
		entityManager.persist(dato);
		transaction.commit();
	}

	public MuRol get(long id) {
		return entityManager.find(MuRol.class, id);
	}

	public void update(MuRol dato) throws Exception {
		transaction.begin();
		entityManager.merge(dato);
		transaction.commit();
	}

	public void updateRolFormulario(MuRolFormulario dato) throws Exception {
		transaction.begin();
		entityManager.merge(dato);
		transaction.commit();
	}

	public void updateRolForulario(MuRolFormulario dato) throws Exception {
		transaction.begin();
		entityManager.merge(dato);
		transaction.commit();
	}

	@SuppressWarnings("unchecked")
	public List<MuRol> getList() {
		return entityManager.createQuery("SELECT r FROM MuRol r WHERE  r.estado = true Order By r.nombre").getResultList();
	}

	@SuppressWarnings("unchecked")
	public MuRol getName(String name) {

		String consulta = "SELECT r FROM MuRol r WHERE r.nombre = :name and r.estado = true";
		Query qu = entityManager.createQuery(consulta).setParameter("name", name);
		List<MuRol> lista = qu.getResultList();
		return lista.isEmpty() ? null : lista.get(0);
	}

	@SuppressWarnings("unchecked")
	public List<MuRolFormulario> getRolFormulario(long id) {
		String consulta = "FROM MuRolFormulario r WHERE r.muRol.rolId = :id ORDER BY r.muFormulario.orden";
		Query qu = entityManager.createQuery(consulta).setParameter("id", id);
		return qu.getResultList();

	}

	@SuppressWarnings("unchecked")
	public List<MuRolFormulario> getRolFormularioDelete(long idRol) {
		String consulta = "SELECT r FROM MuRolFormulario r WHERE r.muRol.rolId = :idRol and r.muRol.estado = true";
		Query qu = entityManager.createQuery(consulta).setParameter("idRol", idRol);
		return qu.getResultList();
	}

	public void deleteRolFormulario(long rolId) throws Exception {
		String sql = "UPDATE MuRolFormulario rf SET rf.estado = false, rf.privilegio = NULL WHERE rf.id.rolId = :rolId";
		transaction.begin();
		Query q = entityManager.createQuery(sql);
		q.setParameter("rolId", rolId);
		q.executeUpdate();
		transaction.commit();
	}

	@SuppressWarnings("unchecked")
	public List<MuRolFormulario> getRolFormularioUser(long id) {
		String consulta = "SELECT r FROM MuRolFormulario r WHERE  r.rol.rolId = :id  " + "ORDER BY r.formulario.posicionColumna , r.formulario.posicionFila   ";
		Query qu = entityManager.createQuery(consulta).setParameter("id", id);
		return qu.getResultList();
	}

}
