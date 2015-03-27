package micrium.user.bean;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;

import micrium.id.FormularioID;
import micrium.user.business.ParametroBL;
import micrium.user.controler.ControlPrivilegio;
import micrium.user.ldap.DescriptorBitacora;
import micrium.user.model.Parametro;
import micrium.user.sys.IControlPrivilegios;
import micrium.user.sys.P;

import org.apache.log4j.Logger;

@ManagedBean
@ViewScoped
public class ParametroForm implements Serializable,IControlPrivilegios {
	private static final long serialVersionUID = 1L;
	private static Logger log = Logger.getLogger(ParametroForm.class);
	private static final int EXIT_ERROR=0;
	private static final int EXIT_OK=1;

	@Inject
	private ParametroBL parametroBL;

	@Inject
	private ControlerBitacora controlerBitacora;

	private List<Parametro> listParms;

	private Parametro parametro = new Parametro();
	private int exit = 1;
	private ControlPrivilegio controlPrivilegio;
	
	@PostConstruct
	public void init() {
		try {
			controlPrivilegio = ControlPrivilegio.getInstanceControl();
			parametro = new Parametro();
			listParms = parametroBL.getParameters();
		} catch (Exception e) {
			log.error("init|Fallo al inicializar la clase. " + e.getMessage());
		}
	}

	public void saveParametro() {
		log.info("[SaveParametro] Guardando Camnios para el parametro:"+parametro.getNombre());
		try {
			String msj = parametroBL.validate(parametro);
			log.info("[RespValidacion]"+msj);
			if(!msj.isEmpty()){
				exit = EXIT_ERROR;
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR, "Mensaje de error", msj));
			}else{
				exit = EXIT_OK;
				parametroBL.updateParametro(parametro);
				P.restartParameter();
				controlerBitacora.update(DescriptorBitacora.PARAMETRO, parametro.getParametroId() + "", parametro.getNombre());
				listParms = parametroBL.getParameters();
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage(FacesMessage.SEVERITY_INFO, "[Ok]", "Operacion Satisfactoria..."));
			}	
		} catch (Exception e) {
			log.error("[SaveParametro]Error al Intentar gauradar los Cambios:"+e.getMessage(),e);
			exit = EXIT_ERROR;
		}
	}
	
	public void editarParametro(int idparm){
		log.info("********************** Editar parametro[ID=" +idparm+" ] ***************");
		try {
			parametro = parametroBL.getParametro(idparm);
			log.info("*** [Datos Iniaciales]"+parametro);
		} catch (Exception e) {
			log.error("[Editar Parametro] Error al Consultar Parametro: "+idparm+": "+e.getMessage(),e);
		}
	}

	public List<Parametro> getListParms() {
		return listParms;
	}

	public void setListParms(List<Parametro> listParms) {
		this.listParms = listParms;
	}

	public Parametro getParametro() {
		return parametro;
	}

	public void setParametro(Parametro parametro) {
		this.parametro = parametro;
	}

	public int getExit() {
		return exit;
	}

	public void setExit(int exit) {
		this.exit = exit;
	}

	@Override
	public boolean isAuthorized(int idAccion) {
		return controlPrivilegio.isAuthorized(FormularioID.FORM_GEST_PARAMETROS, idAccion);
	}
	
}
