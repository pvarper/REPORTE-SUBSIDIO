package micrium.user.business;

import java.io.Serializable;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Named;
import micrium.user.dao.ParametroDAO;
import micrium.user.model.Parametro;

@Named
public class ParametroBL implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Inject
	private  ParametroDAO parametroDao;

	public String validate(Parametro parametro) throws Exception {
		String resp = "";
		if(parametro != null){
			switch (parametro.getTipo()) {
			case Parametro.TIPO_CADENA:{
				if(parametro.getValorCadena() == null || parametro.getValorCadena().trim().isEmpty()){
					resp = "[Error]El valor del Parametro "+parametro.getNombre()+", esta Vacio.. ";
				}
				break;
			}
			case Parametro.TIPO_NUMERICO:{
				if(parametro.getValorNumerico() == null){
					resp = "[Error]El valor del Parametro "+parametro.getNombre()+", esta Vacio.. ";
				}
				break;
			}
			case Parametro.TIPO_FECHA:{
				if(parametro.getValorFecha() == null){
					resp = "[Error]El valor del Parametro "+parametro.getNombre()+", esta Vacio.. ";
				}
				break;
			}
			case Parametro.TIPO_BOOLEANO:{
				if(parametro.getValorBooleano() == null){
					resp = "[Error]El valor del Parametro "+parametro.getNombre()+", esta Vacio.. ";
				}
				break;
			}
			}
		}
		return resp;
	}

	public void updateParametro(Parametro parametro) throws Exception {
		parametroDao.update(parametro);
	}

	public List<Parametro> getParameters() throws Exception {
		return parametroDao.getList();
	}

	public Parametro getParametro(int idParametro) throws Exception {
		return parametroDao.get(idParametro);
	}

}
