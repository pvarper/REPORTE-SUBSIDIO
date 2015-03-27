package micrium.user.model;

import java.io.Serializable;
import javax.persistence.*;
import java.sql.Timestamp;

/**
 * The persistent class for the MU_BITACORA database table.
 * 
 */
@Entity
@Table(name = "MU_BITACORA")
public class MuBitacora implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name = "MU_BITACORA_ID_GENERATOR", sequenceName = "SEQ_MU_BITACORA", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "MU_BITACORA_ID_GENERATOR")
	private long id;

	private String accion;

	@Column(name = "DIRECCION_IP")
	private String direccionIp;

	private Timestamp fecha;

	private String formulario;

	private String usuario;

	public MuBitacora() {
	}

	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getAccion() {
		return this.accion;
	}

	public void setAccion(String accion) {
		this.accion = accion;
	}

	public String getDireccionIp() {
		return this.direccionIp;
	}

	public void setDireccionIp(String direccionIp) {
		this.direccionIp = direccionIp;
	}

	public Timestamp getFecha() {
		return this.fecha;
	}

	public void setFecha(Timestamp fecha) {
		this.fecha = fecha;
	}

	public String getFormulario() {
		return this.formulario;
	}

	public void setFormulario(String formulario) {
		this.formulario = formulario;
	}

	public String getUsuario() {
		return this.usuario;
	}

	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}

}