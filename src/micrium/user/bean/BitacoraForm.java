package micrium.user.bean;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;

import micrium.user.business.BitacoraBL;
import micrium.user.model.MuBitacora;

import org.apache.log4j.Logger;

@ManagedBean
@ViewScoped
public class BitacoraForm implements Serializable {

	private static final long serialVersionUID = 1L;

	public static Logger log = Logger.getLogger(BitacoraForm.class);

	@Inject
	private BitacoraBL logBl;

	private List<MuBitacora> listBitacora;

	@PostConstruct
	public void init() {
		try {
			listBitacora = logBl.listBitacora();
		} catch (Exception e) {
			log.error("[init] Fallo al iniciar el bean.", e);
		}
	}

	public List<MuBitacora> getListBitacora() {
		return listBitacora;
	}

	public void setListBitacora(List<MuBitacora> listBitacora) {
		this.listBitacora = listBitacora;
	}

}
