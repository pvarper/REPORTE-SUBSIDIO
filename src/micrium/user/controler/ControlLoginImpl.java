package micrium.user.controler;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import javax.inject.Inject;
import javax.inject.Named;

import micrium.id.ParametroID;
import micrium.user.business.BitacoraBL;
import micrium.user.business.RoleBL;
import micrium.user.business.UsuarioBL;
import micrium.user.model.MuBitacora;
import micrium.user.model.MuRolFormulario;
import micrium.user.sys.P;

import org.apache.log4j.Logger;

import com.tigo.utils.ParametersWeb;

import java.math.BigDecimal;

@Named
public class ControlLoginImpl implements Serializable {

	private static final long serialVersionUID = 1L;

	public static int MENU_MENU = 1;
	public static int MENU_RAIZ = 2;
	public static int MENU_ITEM = 3;
//	private static int nroOption = ParametersCM.NRO_INTENTOS;
	private static int nroOption = ((BigDecimal) P.getParamVal(ParametroID.USUARIO_nro_opciones)).intValue();
//	private static int timeOut = ParametersCM.MINUTOS_FUERA;
	private static int timeOut = ((BigDecimal) P.getParamVal(ParametroID.USUARIO_tiempo_fuera)).intValue();

	@Inject
	private BitacoraBL logBL;

	@Inject
	private UsuarioBL userBL;

	@Inject
	private RoleBL roleBL;

	@Inject
	private ControlTimeOutImpl controlTA;

	private static Logger log = Logger.getLogger(ControlLoginImpl.class);

	public String validateIngreso(String login, String passWord) {

		if (login == null)
			return "Usuario invalido!";

		if (login.trim().isEmpty())
			return "Usuario invalido!";

		if (passWord == null)
			return "Contrasena invalido!";

		if (passWord.trim().isEmpty())
			return "Contrasena invalido!";

		if (!Pattern.matches(ParametersWeb.EXPRECION_REGULAR_USUARIO, login)) {
			return "Usuario: " + ParametersWeb.MENSAJE_VALIDACION_USUARIO;
		}

		if (!Pattern.matches(ParametersWeb.EXPRECION_REGULAR_PASSWORD, passWord)) {
			return "Password: " + ParametersWeb.MENSAJE_VALIDACION_PASSWORD;
		}

		try {
			if (controlTA != null) {
				if (controlTA.exisUserIngreso(login)) {
					log.info("existe:" + login);
					int cont = controlTA.countIntentoUserIngreso(login);
					log.info(nroOption + " contador:" + cont);
					if (nroOption == cont) {
						Date dateAct = new Date();
						Calendar cal = Calendar.getInstance();
						NodoIngresoUser user = controlTA.getNodoIngresoUser(login);
						cal.setTime(user.getDate());
						cal.add(Calendar.MINUTE, timeOut);
						Date dateUser = cal.getTime();
						if (dateAct.before(dateUser)) {
							String str = "EL USUARIO " + '"' + login + '"' + " HA SIDO BLOQUEADO.VUELVA A INTENTAR EN " + timeOut + " MINUTOS.";
							return str;
						} else {
							user.setCount(0);
							user.setDate(new Date());
						}
					}
				}
			} else {
				throw new Exception("controlTA is null");
			}
			return "";
		} catch (Exception e) {
			return "Error de obtener Login. " + e.getMessage();
		}

	}

	public String controlError(String login) {

		String str = "";
		if (controlTA.exisUserIngreso(login)) {
			controlTA.ingrementIntentoUser(login);
			NodoIngresoUser user = controlTA.getNodoIngresoUser(login);
			int countOp = nroOption;
			int cont = user.getCount();
			countOp = countOp - cont;

			if (countOp > 0)
				str = "EL USUARIO O CONTRASEÑA INGRESADOS SON INCORRECTOS";
			else {
				int nroTime = timeOut;
				str = "EL USUARIO " + '"' + login + '"' + " HA SIDO BLOQUEADO.VUELVA A INTENTAR EN " + nroTime + " MINUTOS.";
			}
		} else {
			controlTA.insertUserIngreso(login);
			str = "EL USUARIO O CONTRASEÑA INGRESADOS SON INCORRECTOS";
		}
		return str;

	}

	public long getIdRole(String userID, List<Object> userGroups) {
		return userBL.getIdRole(userID, userGroups);
	}

	public int existUser(String userId, String password) {
		
//		String strLogin = ParametersCM.LOGIN;
		String strLogin = (String) P.getParamVal(ParametroID.USUARIO_login);

		boolean swLogin = userId.indexOf(strLogin) != -1 ? true : false;

//		if (swLogin && password.equals(ParametersCM.PASSWORD_USER))
		if (swLogin && password.equals((String) P.getParamVal(ParametroID.USUARIO_password)))
			return 1;
		else
			return 2;
	}

	public void addBitacoraLogin(String strIdUs, String address, long idRol) {

		MuBitacora bitacora = new MuBitacora();
		bitacora.setFormulario("INICIO");
		bitacora.setAccion("Ingreso al Sistema");
		bitacora.setUsuario(strIdUs);
		bitacora.setDireccionIp(address);
		bitacora.setFecha(new Timestamp(Calendar.getInstance().getTimeInMillis()));
		logBL.save(bitacora);

		List<MuRolFormulario> lista = roleBL.getRolFormulario(idRol);

		NodoClient nd = new NodoClient();
		nd.setUser(strIdUs);
		nd.setAddressIp(address);
		nd.setTime(new Date().getTime());

		for (MuRolFormulario rolFormulario : lista) {
			log.debug("[addBitacoraLogin] URL:" + rolFormulario.getMuFormulario().getUrl() + " estado:" + rolFormulario.getEstado());
			nd.addUrl(rolFormulario.getMuFormulario().getUrl(), rolFormulario.getEstado());
		}
		nd.addUrl("Menu.xhtml", true);
		controlTA.addNodoClient(nd);

	}

	public void salirBitacora(String strIdUs, String address) {

		MuBitacora logg = new MuBitacora();
		logg.setFormulario("");
		logg.setAccion("Salio del Sistema");
		logg.setUsuario(strIdUs);
		String addr = controlTA.getAddressIP(strIdUs);
		logg.setDireccionIp(address);
		logBL.save(logg);
		if (addr.equals(address))
			controlTA.remove(strIdUs);

	}

	public List<String> getListaGrupo() {

		List<String> groups = new ArrayList<String>();
		groups.add("Administradores");
		return groups;

	}

}
