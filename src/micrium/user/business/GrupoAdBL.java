package micrium.user.business;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import micrium.user.dao.GrupoAdDAO;
import micrium.user.model.MuGrupoAd;
import micrium.user.model.MuRol;

@SuppressWarnings("serial")
@Named
public class GrupoAdBL implements Serializable {

	@Inject
	private GrupoAdDAO dao;

	public String validate(MuGrupoAd user, String idStr) {

		if (user.getNombre().isEmpty()) {
			return "El campo Nombre esta Vacio";
		}

		MuGrupoAd usAux = dao.getGroupName(user.getNombre());
		if (usAux == null)
			return "";

		if (idStr != null && !idStr.isEmpty()) {
			int id = Integer.parseInt(idStr);
			if (id == usAux.getGrupoId())
				if (user.getNombre().equals(usAux.getNombre()))
					return "";
		}

		return "este nombre de grupo ya existe";
	}

	public void saveGroupRole(MuGrupoAd group, int idRole) throws Exception {
		MuRol rol = new MuRol();
		rol.setRolId(idRole);
		group.setMuRol(rol);
		group.setEstado(true);
		dao.save(group);
	}

	public void updateGroup(MuGrupoAd group, int idRole) throws Exception {
		MuRol rol = new MuRol();
		rol.setRolId(idRole);

		MuGrupoAd groupAux = dao.get(group.getGrupoId());
		groupAux.setNombre(group.getNombre());
		groupAux.setDetalle(group.getDetalle());
		groupAux.setMuRol(rol);
		dao.update(groupAux);
	}

	public void deleteGroup(long idGroup) throws Exception {
		MuGrupoAd user = dao.get(idGroup);
		user.setEstado(false);
		dao.update(user);
	}

	public List<MuGrupoAd> getGroups() {
		return dao.getList();
	}

	public MuGrupoAd getGroup(int idGroup) {
		return dao.get(idGroup);
	}
}