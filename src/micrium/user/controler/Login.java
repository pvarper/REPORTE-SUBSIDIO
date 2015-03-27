package micrium.user.controler;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.constraints.Size;

import micrium.id.ParametroID;
import micrium.user.dao.FormularioDAO;
import micrium.user.filter.loginFilter;
import micrium.user.ldap.ActiveDirectory;
import micrium.user.ldap.LdapContextException;
import micrium.user.model.MuFormulario;
import micrium.user.sys.P;

import org.apache.log4j.Logger;
//import org.primefaces.component.menuitem.MenuItem;
//import org.primefaces.component.submenu.Submenu;
//import org.primefaces.model.DefaultMenuModel;
//import org.primefaces.model.MenuModel;
import org.primefaces.model.menu.DefaultMenuItem;
import org.primefaces.model.menu.DefaultMenuModel;
import org.primefaces.model.menu.DefaultSubMenu;
import org.primefaces.model.menu.MenuElement;
import org.primefaces.model.menu.MenuModel;




import sun.misc.BASE64Decoder;


//import com.tigo.utils.ParametersMU;
import com.tigo.utils.SysMessage;

@ManagedBean(name = "login")
@SessionScoped
public class Login implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	ControlPrivilegio controlP;

	@Inject
	ControlLoginImpl controler;

	@Inject
	private FormularioDAO daoFormulario;

	@Size(min = 2, max = 20, message = "Se requiere un minimo de 2 y maximo de 20 caracteres")
	private String userId;

	@Size(min = 2, max = 50, message = "Se requiere un minimo de 5 y maximo de 50 caracteres")
	private String password;

	// private String messageError;

	private MenuModel model;

	private static final Logger log = Logger.getLogger(Login.class);

	public Login() {
		this.userId = "";
		this.password = "";

	}

	public String logout() {
		HttpServletRequest request = (HttpServletRequest) FacesContext
				.getCurrentInstance().getExternalContext().getRequest();
		HttpSession sesion = request.getSession();
		sesion.setAttribute("TEMP$ACTION_MESSAGE_ID", "");
		sesion.setAttribute("TEMP$USER_NAME", "");
		sesion.setAttribute("TEMP$GROUP", "");
		sesion.setAttribute("controlPrivilegio", null);
		sesion.invalidate();
		return "/";
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public String verifyLogin() {
		try {
			decryptPasswordBase64();
		} catch (Exception e1) {
			log.warn("Error: PassDecoder..!");
		}
		String validacion = controler.validateIngreso(userId, password);
		if (!validacion.isEmpty()) {
			// messageError = validacion;
			SysMessage.error(validacion);
			return "";

		}

		boolean banderaDesarrollo = false;
		try {
			String thisIp = InetAddress.getLocalHost().getHostAddress();
			log.info("[verifyLogin] Ip del servidor: " + thisIp);
			banderaDesarrollo = thisIp.trim().equalsIgnoreCase(
					((String) P.getParamVal(ParametroID.IP_DESARROLLO)).trim());
		} catch (UnknownHostException e) {
		}
		if (!banderaDesarrollo) {
			banderaDesarrollo = !(Boolean) P
					.getParamVal(ParametroID.USUARIO_sw_active_directory);
		}
		try {

			int valid = banderaDesarrollo ? controler.existUser(userId,
					password) : ActiveDirectory
					.validarUsuario(userId, password);
			if (valid == ActiveDirectory.EXIT_USER) {

				List groups = banderaDesarrollo ? controler.getListaGrupo()
						: ActiveDirectory.getListaGrupos(userId);

				if (groups.size() > 0) {

					long idRol = controler.getIdRole(userId, groups);
					log.info("[verifyLogin] User: " + userId + ", Rol:" + idRol);
					if (idRol > 0) {

						HttpServletRequest request = (HttpServletRequest) FacesContext
								.getCurrentInstance().getExternalContext()
								.getRequest();

						request.getSession().setAttribute("TEMP$USER_NAME",
								userId);

						String remoteAddr = ((HttpServletRequest) FacesContext
								.getCurrentInstance().getExternalContext()
								.getRequest()).getRemoteAddr();
						controler.addBitacoraLogin(userId, remoteAddr, idRol);
						cargarv2(idRol);
						/***************/
						controlP.cargarAccionPrivilegios(daoFormulario
								.findPrivilegios(idRol));
						HttpSession sesion = request.getSession();
						sesion.setAttribute(
								ControlPrivilegio.CONTROL_PRIVILEGIO_BEAN,
								this.controlP);
						/***************/
						return "/view/menu.xhtml";
					} else {
						SysMessage
								.error("Usted no esta registrado para ningun Rol");
						return "";
					}

				} else {
					SysMessage.error("Usted no tiene grupo de trabajo");
					return "";
				}
			} else {
				validacion = controler.controlError(userId);
				SysMessage.error(validacion);
				return "";
			}
		} catch (LdapContextException e) {
			SysMessage.error("Error Login", e.getMessage());
			log.error("Error en LDAP:"+e.getMessage());
			return "";
		}
	}

	private void cargarv2(long roleId) {
		model = new DefaultMenuModel();
		List<MuFormulario> lform = daoFormulario.findPadres(roleId);
		for (MuFormulario form : lform) {
			model.addElement((DefaultSubMenu) cargarv2(form, roleId));
		}

		DefaultSubMenu submenu = new DefaultSubMenu();
		submenu.setLabel("Opciones");
		DefaultMenuItem item = new DefaultMenuItem();
		item.setValue("Manual de Usuario");
		String pathDoc = loginFilter.pathRaiz + "resources/Manual.pdf";
		String strUrlDoc = "window.open('" + pathDoc + "'); return false;";
		item.setOnclick(strUrlDoc);
		item.setIcon("ui-icon ui-icon-document");
		submenu.addElement(item);

		// item = new DefaultMenuItem();
		// item.setValue("Cerrar Sesión");
		// item.setIcon("ui-icon ui-icon-close");
		// item.setUrl("/Logout");
		// submenu.addElement(item);
		model.addElement(submenu);
	}

	private MenuElement cargarv2(MuFormulario form, long roleId) {
		List<MuFormulario> lHijos = daoFormulario.findHijos(form.getId(),
				roleId);
		if (lHijos != null && !lHijos.isEmpty()) {
			DefaultSubMenu submenu = new DefaultSubMenu();
			submenu.setLabel(form.getNombre());
			for (MuFormulario hijo : lHijos) {
				submenu.addElement(cargarv2(hijo, roleId));
			}
			return submenu;
		} else {
			if ((form.getUrl() == null) && (lHijos != null)
					&& (lHijos.isEmpty())) {
				DefaultSubMenu submenu = new DefaultSubMenu();
				submenu.setLabel(form.getNombre());
				return submenu;
			}
			DefaultMenuItem item = new DefaultMenuItem();
			item.setValue(form.getNombre());
			item.setUrl(form.getUrl());
			return item;
		}
	}

	public MenuModel getModel() {
		return model;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	private void decryptPasswordBase64() throws Exception {
		if(password != null){
			BASE64Decoder decoder = new BASE64Decoder();
			this.password = new String(decoder.decodeBuffer(password), "UTF-8");
		}
	}
	
}
