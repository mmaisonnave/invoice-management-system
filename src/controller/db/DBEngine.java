package controller.db;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import exception.InvalidBudgetException;
import exception.InvalidClientException;
import javafx.beans.property.SimpleStringProperty;

/**
 * Clase que permite un manejo transparente de la base de datos
 * 'programa_facturacion_mh'. Brindando operaciones para manipular Clientes,
 * Presupuestos, Transacciones y Pagos.
 * 
 * DESCRIPCIÓN DE LA BASE DE DATOS: +-----------------------------------+ |
 * Tables_in_programa_facturacion_mh | +-----------------------------------+ |
 * Cliente | | Concepto_presupuesto | | Cuenta_corriente | | Presupuesto | |
 * Transaccion | +-----------------------------------+
 * 
 * CONFIGURACIÓN POR DEFECTO A LA BASE DE DATOS: Por defecto la conección se
 * establece con el jdbc Driver para mysql. La conección es con el usuario root,
 * conectado a 192.168.3.107 directo a la base de datos
 * 'programa_facturacion_mh'.
 * 
 * @author maiso
 *
 */
public class DBEngine {
	protected final String myDriver = "com.mysql.jdbc.Driver";
//	protected final String myUrl  = "jdbc:mysql://192.168.0.4/programa_facturacion_mh"; 
	protected final String myUrl = "jdbc:mysql://127.0.0.1/programa_facturacion_mh?useSSL=false";

	protected int ProximoNumeroPresupuesto; // para poder ofrecer el proximo numero presupuesto.
	protected Connection conn;

	public DBEngine(String url) {
		String myUrl = "jdbc:mysql://" + url + "/programa_facturacion_mh";
		try {

			Class.forName(myDriver);
			conn = DriverManager.getConnection(myUrl, "root", "maisonnave1");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public DBEngine() {
		try {

			Class.forName(myDriver);
			conn = DriverManager.getConnection(myUrl, "root", "maisonnave1");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// private int getProximoNumeroPresupuesto(){
	// return this.ProximoNumeroPresupuesto;
	// }

	// ==================================================================================================================================
	// ** ** ** ** CLIENTES ** ** ** **
	// ==================================================================================================================================

	/**
	 * Método que consulta la db para saber cuantos clientes habilitados hay.
	 * 
	 * @return El entero que representa la cantidad de clientes habilitados, -1 uno
	 *         de haber un problema (el caso donde no haya conexion con la base de
	 *         datos por ej).
	 */
	public int cantidadClientesHabilitados() {
		int cantidad = -1;
		String query = "SELECT (COUNT(*)) AS Cant_Clientes_hab from Cliente WHERE Habilitado='S'";
		Statement st;
		try {
			st = conn.createStatement();
			ResultSet rs = st.executeQuery(query);
			if (rs.next()) {
				cantidad = rs.getInt("Cant_Clientes_hab");
			}
			st.close();
		} catch (SQLException e) {

			e.printStackTrace();
		}
		return cantidad;

	}

	/**
	 * El método consulta a la base de datos por el único cliente que tiene ese dado
	 * Codigo_Cliente, si el mismo no aparece retorna un objeto nulo. Si el cliente
	 * es encontrado, se recuperan sus atributos de la base de datos y se los
	 * retornan a la clase que llamo el método. Obs: Todos los atributos pueden ser
	 * nulos, menos el codigo_cliente y el CUIT.
	 * 
	 * @param Codigo_Cliente Número de identificación ÚNICO del cliente.
	 * @return Objeto de tipo Cliente, con todos sus parámetros cargados:
	 *         Codigo_Cliente (NOT NULL), CUIT (NOT
	 *         NULL),Denominacion,Direccion,Localidad,Telefono,Email, Condicion_iva,
	 *         Habilitado.
	 */
	public Cliente getCliente(int Codigo_Cliente) {
		String query = "SELECT * FROM Cliente WHERE Codigo_Cliente = " + Codigo_Cliente;
		Cliente toReturn = null;
		// create the java statement
		Statement st;
		try {
			st = conn.createStatement();
			ResultSet rs = st.executeQuery(query);
			if (rs.next()) {
				// int Codigo_Cliente, String CUIT, String denominacion, String direccion,
				// String localidad,
				// String telefono, String correoElectronico, String condicionIva, String
				// habilitado
				toReturn = new Cliente(rs.getInt("Codigo_Cliente"), rs.getString("CUIT"), rs.getString("Denominacion"),
						rs.getString("Direccion"), rs.getString("Localidad"), rs.getString("Telefono"),
						rs.getString("Email"), rs.getString("Condicion_iva"), rs.getString("Habilitado"));
			}
			st.close();
		} catch (SQLException e) {

			e.printStackTrace();
		}
		return toReturn;
	}

	// TMP
	public List<Cliente> getClientesHabilitados() {
		Cliente aux;
		List<Cliente> lista = new ArrayList<Cliente>();
		String query = "SELECT * " + "FROM Cliente " + "WHERE Habilitado='S' ORDER BY Denominacion";

		Statement st;
		try {
			st = conn.createStatement();
			ResultSet rs = st.executeQuery(query);
			while (rs.next()) {
				// int Codigo_Cliente, String CUIT, String denominacion, String direccion,
				// String localidad,
				// String telefono, String correoElectronico, String condicionIva, String
				// habilitado
				aux = new Cliente(rs.getInt("Codigo_Cliente"), rs.getString("CUIT"), rs.getString("Denominacion"),
						rs.getString("Direccion"), rs.getString("Localidad"), rs.getString("Telefono"),
						rs.getString("Email"), rs.getString("Condicion_iva"), rs.getString("Habilitado"));
				lista.add(aux);
			}
			st.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return lista;
	}

	// TMP
	/**
	 * Busca en la base de Clientes por un cliente que contenga en algun lugar del
	 * campo "denominacion", el String pasado por parámetro ('denominacion'). No es
	 * sensible a minusculas y mayusculas.
	 * 
	 * Retorna todo aquel cliente que contenga el String buscado. Si no encuentra
	 * ningun cliente retorna una lista vacía.
	 * 
	 * @param denominacion Un String que representa la busqueda del cliente, se
	 *                     buscara en el campo "Denominacion", de la base de datos
	 *                     "Cliente" por un String que coincida con el pasado por
	 *                     parámetro.
	 * @return Una lista conteniendo los Clientes que coinciden con la busqueda, o
	 *         una lista vacía si no existe ninguna coincidencia.
	 */
	public List<Cliente> buscarCliente(String denominacion) {
		Cliente aux;
		List<Cliente> lista = new ArrayList<Cliente>();
		String query = "SELECT * " + "FROM Cliente " + "WHERE Denominacion LIKE '%" + denominacion + "%' "
				+ "ORDER BY Denominacion ASC ";

		Statement st;
		try {
			st = conn.createStatement();
			ResultSet rs = st.executeQuery(query);
			while (rs.next()) {
				// int Codigo_Cliente, String CUIT, String denominacion, String direccion,
				// String localidad,
				// String telefono, String correoElectronico, String condicionIva, String
				// habilitado
				aux = new Cliente(rs.getInt("Codigo_Cliente"), rs.getString("CUIT"), rs.getString("Denominacion"),
						rs.getString("Direccion"), rs.getString("Localidad"), rs.getString("Telefono"),
						rs.getString("Email"), rs.getString("Condicion_iva"), rs.getString("Habilitado"));
				lista.add(aux);
			}
			st.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return lista;
	}

	/**
	 * Utiliza el String pasado por parámetro para buscar clientes. Todo aquel
	 * Cliente que tenga un CUIT coincidente, o que contenga al CUIT pasado por
	 * parámetro sera retornado en la lista. Para la busqueda no es necesario pasar
	 * un CUIT completo, busca por coincidencias en todo el String, y retorna todas
	 * las coincidencias en una lista. Si no hay coincidencias retorna una lsita
	 * vacía.
	 * 
	 * @param CUIT el String que se utilizará para la búsqueda, puede ser un CUIT
	 *             parcial, no necesita ser completo.
	 * @return Retorna una lista de Clientes con todos aquellos que en su CUIT
	 *         contengan al CUIT buscado, retornará una lista vacía en caso de que
	 *         no haya coincidencias.
	 */
	public List<Cliente> buscarCUIT(String CUIT) {
		Cliente aux;
		List<Cliente> lista = new ArrayList<Cliente>();
		String query = "SELECT * " + "FROM Cliente " + "WHERE CUIT LIKE '%" + CUIT + "%' "
				+ "ORDER BY Denominacion ASC ";

		Statement st;
		try {
			st = conn.createStatement();
			ResultSet rs = st.executeQuery(query);
			while (rs.next()) {
				aux = new Cliente(rs.getInt("Codigo_Cliente"), rs.getString("CUIT"), rs.getString("Denominacion"),
						rs.getString("Direccion"), rs.getString("Localidad"), rs.getString("Telefono"),
						rs.getString("Email"), rs.getString("Condicion_iva"), rs.getString("Habilitado"));
				lista.add(aux);
			}
			st.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return lista;
	}

	/**
	 * Recibe un cliente por parámetro y lo inserta en la base de datos. UTiliza
	 * todos sus atributos a excepción del 'Codigo_Cliente', el cual hasta ese
	 * momento debería ser inválido ya que es un numero asociado a la base de datos.
	 * Luego de insertar el cliente en la base de datos, recupera el su número de
	 * cliente (Codigo_Cliente), el cual lo identifica únivocamente en la base de
	 * datos, y lo inserta en el objeto pasado por parámetro.
	 * 
	 * @param cliente Recibe un Objeto tipo cliente, y lo inserta en la base de
	 *                datos. Luego de insertar actualiza su Codigo_Cliente, por
	 *                aquel con el cuál fue cargado en la base de datos.
	 * @return True si fue insertado correctamente. False si hubo algún error (Base
	 *         de datos desconectada, problemas de conexión).
	 */
	public boolean agregarCliente(Cliente cliente) {
		String query = "INSERT INTO Cliente "
				+ "( CUIT, Denominacion, Direccion, Localidad, Telefono, Email, Habilitado, Condicion_iva ) "
				+ "VALUES (?,?,?,?,?,?,?,?)";

		// System.out.println("en la DB" + cliente.getCondicionIva());

		try {
			PreparedStatement preparedStmt = conn.prepareStatement(query);
//TODO:  Que pasa si el cliente tiene atributos nulos?
			preparedStmt.setString(1, cliente.getCuit());
			preparedStmt.setString(2, cliente.getDenominacion());
			preparedStmt.setString(3, cliente.getDireccion());
			preparedStmt.setString(4, cliente.getLocalidad());
			preparedStmt.setString(5, cliente.getTelefono());
			preparedStmt.setString(6, cliente.getCorreoElectronico());
			preparedStmt.setString(7, cliente.getHabilitado());
			preparedStmt.setString(8, cliente.getCondicionIva());
			preparedStmt.execute();

			// OBTENIENDO EL Codigo_Cliente del ultimo cliente insertado.

			String query_aux = "SELECT LAST_INSERT_ID() AS ultimo_codigo_cliente";
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery(query_aux);
			int codigo_cliente = -1;
			if (rs.next())
				codigo_cliente = rs.getInt("ultimo_codigo_cliente");
			cliente.actualizarCodigoCliente(codigo_cliente);
			st.close();

			// AGREGAMOS LA CUENTA CORRIENTE ASOCIADA A ESE CLIENTE
			query = "INSERT INTO Cuenta_corriente (Codigo_cliente, Monto) VALUES (?,0.0) ;";
			try {
				preparedStmt = conn.prepareStatement(query);
				preparedStmt.setInt(1, cliente.getCodigoCliente());
				preparedStmt.executeUpdate();
				return true;
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return true;

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return false;
	}

	/**
	 * Elimina un cliente de la base de datos. Retorna un objeto conteniendo el
	 * Cliente eliminado, con su Codigo_Cliente anulado (ya que ese Codigo_Cliente
	 * no existe más en la base de datos).
	 * 
	 * @param Codigo_Cliente Código del cliente buscado para ser eliminado, cada
	 *                       código representa a un único cliente.
	 * @return Retorna el cliente eliminado, si no encontró ningún cliente con el
	 *         código pasado por parámetro retorna null.
	 */
	public Cliente eliminarCliente(int Codigo_Cliente) {
		Cliente toReturn = this.getCliente(Codigo_Cliente);
		if (toReturn != null) {
			String query = "DELETE FROM Cliente " + "WHERE Codigo_Cliente = ?";
			PreparedStatement preparedStmt;
			try {
				preparedStmt = conn.prepareStatement(query);
				preparedStmt.setInt(1, Codigo_Cliente);
				preparedStmt.execute();
				toReturn.invalidarCliente();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return toReturn;
	}

	/**
	 * Método que dado un objeto cliente cambia su estado en la base de datos
	 * (utilizando el código de cliente del mismo). Cambia su atributo Habilitado a
	 * 'N'.
	 * 
	 * @param c Recibe un objeto Cliente del cuál extrae el código de cliente para
	 *          acceder a la base de datos.
	 * @return Retorna true si salió todo bien.
	 * @throws InvalidClientException Si él código de cliente es inválido (el
	 *                                cliente no existiría en la base de datos).
	 */
	private boolean inhabilitarCliente(Cliente c) throws InvalidClientException {
		if (!c.esValidoCodigoCliente()) {
			throw new InvalidClientException("Código de cliente inválido (método inhabilitar cliente)");
		}
		PreparedStatement preparedStmt;
		if (c.esValidoCodigoCliente()) {
			String update = "UPDATE Cliente SET Habilitado = 'N' WHERE Codigo_Cliente = ?";
			try {
				preparedStmt = conn.prepareStatement(update);
				preparedStmt.setInt(1, c.getCodigoCliente());
				preparedStmt.executeUpdate();
				return true;
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		return false;
	}

	/**
	 * Método que dado un objeto cliente cambia su estado en la base de datos
	 * (utilizando el código de cliente del mismo). Cambia su atributo Habilitado a
	 * 'S'.
	 * 
	 * @param c Recibe un objeto Cliente del cuál extrae el código de cliente para
	 *          acceder a la base de datos.
	 * @return Retorna true si salió todo bien.
	 * @throws InvalidClientException InvalidClientException Si él código de cliente
	 *                                es inválido (el cliente no existiría en la
	 *                                base de datos).
	 */
	private boolean habilitarCliente(Cliente c) throws InvalidClientException {
		if (!c.esValidoCodigoCliente()) {
			throw new InvalidClientException("Código de cliente inválido (método habilitar cliente)");
		}
		PreparedStatement preparedStmt;
		if (c.esValidoCodigoCliente()) {
			String update = "UPDATE Cliente SET Habilitado = 'S' WHERE Codigo_Cliente = ?";
			try {
				preparedStmt = conn.prepareStatement(update);
				preparedStmt.setInt(1, c.getCodigoCliente());
				preparedStmt.executeUpdate();
				return true;
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		return false;
	}

	/**
	 * Método que actualiza en la base de datos el estado del cliente. Actualiza
	 * todos sus datos: - CUIT - Denominacion - Direccion - Localidad - Telefono -
	 * Email - Habilitado - Condicion_iva
	 * 
	 * OBSERVACIÓN: Si el cliente no existe en la base de datos, el método no hace
	 * NADA.
	 * 
	 * @param c Recibe el objeto Cliente a actualizar, utiliza el Codigo_cliente del
	 *          objeto para acceder a la base de datos y cambiar TODOS los datos
	 *          sobre el cliente.
	 * 
	 */
	public void actualizarCliente(Cliente c) {
		PreparedStatement preparedStmt;
		if (c.esValidoCodigoCliente()) {
			String update = "UPDATE Cliente SET CUIT = ?, Denominacion = ?, Direccion = ?, "
					+ "Localidad = ?, Telefono = ?, Email = ?, Habilitado = ?, Condicion_iva = ?  WHERE Codigo_Cliente = ?";
			try {
				preparedStmt = conn.prepareStatement(update);
				preparedStmt.setString(1, c.getCuit());
				preparedStmt.setString(2, c.getDenominacion());
				preparedStmt.setString(3, c.getDireccion());
				preparedStmt.setString(4, c.getLocalidad());
				preparedStmt.setString(5, c.getTelefono());
				preparedStmt.setString(6, c.getCorreoElectronico());
				preparedStmt.setString(7, c.getHabilitado());
				preparedStmt.setString(8, c.getCondicionIva());
				preparedStmt.setInt(9, c.getCodigoCliente());
				preparedStmt.executeUpdate();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else
			System.out.println("[WARNING] Se llamó al método actualizarCliente con un cliente inválido.");
	}

	// ==================================================================================================================================
	// ** ** ** ** PRESUPUESTOS ** ** ** **
	// ==================================================================================================================================
	/**
	 * Método que busca en la base de datos con una serie de parámetros, y retorna
	 * una lista de presupuestos que coinciden con la busqueda. Los parámetros que
	 * busca son: - denominacion - cuit - fechas desde - fecha hasta Obs: Busca por
	 * la coincidencia de TODOS los parámetros.
	 * 
	 * Obs2: Si ALGUNO De los campos es NULO la busqueda no se puede hacer y el
	 * método no hace nada excepto imprimir un WARNING.
	 * 
	 * @param denom La denominación a buscar en la base de datos, si String vacío
	 *              hace match con TODAS las denominaciones.
	 * @param cuit  cuit a buscar en la base de datos, busca en cualquier lugar del
	 *              campo cuit, es indistinto poner el principio o el final del
	 *              cuit. Si String vacío hace match con toda la base de datos.
	 * @param desde Fecha tope inferior en la busqueda, fecha requerida.
	 * @param hasta Fecha tope máximo para la busqueda, campo requerido.
	 * @return Retorna una lista de presupuestos que coinciden con la parte de la
	 *         denominación y cuit recibido, solo presupuestos entre la fecha
	 *         'desde' 'hasta'.
	 */
	public List<Presupuesto> BuscarDesdeHasta(String denom, String cuit, String desde, String hasta) {
		List<Presupuesto> toReturn = new ArrayList<Presupuesto>();
		if (denom == null || cuit == null || desde == null || hasta == null) {
			System.out.println("[WARNING] Uno de los Strings pasados para la consulta BuscarDesdeHasta son nulos.");
		} else {

			Presupuesto aux;
			Statement st;

			String query = "SELECT * FROM Presupuesto INNER JOIN Cliente ON Presupuesto.Codigo_cliente = Cliente.Codigo_cliente "
					+ "WHERE Presupuesto.Efectivo = 'S' AND Cliente.Denominacion LIKE '%" + denom + "%' AND "
					+ "Cliente.CUIT LIKE '%" + cuit + "%' AND " + "Fecha BETWEEN '" + desde + "' AND '" + hasta
					+ "' ORDER BY Cliente.Denominacion ASC;";

			try {
				st = conn.createStatement();
				ResultSet rs = st.executeQuery(query);
				while (rs.next()) { // List<Concepto> conceptos, Cliente cliente, boolean efectivo, float alicuota,
									// double Subtotal, Date fecha
					aux = new Presupuesto(this.getConceptos(rs.getInt("Nro_Presupuesto")),
							this.getCliente(rs.getInt("Codigo_Cliente")),
							(rs.getString("Efectivo").equals("S") ? true : false), rs.getFloat("Alicuota"),
							rs.getDouble("Subtotal"), rs.getDate("Fecha"), rs.getInt("Mes"));
					aux.actualizarNroPresupuesto(rs.getInt("Nro_Presupuesto"));
					toReturn.add(aux);
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return toReturn;
	}

	/**
	 * Método que busca presupuestos para un cliente dado (cuit y denominación por
	 * parametro) para una fecha especifica 'fecha_actual'. Obs: si cualquiera de
	 * los parametros es null el método no hace nada, solo imprime un warning.
	 * 
	 * @param denom        Denominación para buscar en la base de datos, si el
	 *                     string es vacío hace match con toda la base de datos.
	 * @param cuit         Cuit a buscar en la base de datos, puede hacer match con
	 *                     cualquier parte del cuit. Si el string es vacío hace
	 *                     match con toda la base de datos.
	 * @param fecha_actual Fecha exacta en la cuál se busca el presupuesto.
	 * @return La lista que coincide con todos los parámetros de busqueda.
	 * 
	 */

	public List<Presupuesto> BuscarFechaExacta(String denom, String cuit, String fecha_actual) {
		List<Presupuesto> toReturn = new ArrayList<Presupuesto>();
		if (denom == null || cuit == null || fecha_actual == null) {
			System.out.println("[WARNING] Uno de los Strings pasados para la consulta BuscarDesdeHasta son nulos.");
		} else {

			Presupuesto aux;
			Statement st;

			String query = "SELECT * FROM Presupuesto INNER JOIN Cliente ON Presupuesto.Codigo_cliente = Cliente.Codigo_cliente "
					+ "WHERE Presupuesto.Efectivo = 'S' AND Cliente.Denominacion LIKE '%" + denom + "%' AND "
					+ "Cliente.CUIT LIKE '%" + cuit + "%' AND " + "Fecha = '" + fecha_actual + "';";

			try {
				st = conn.createStatement();
				ResultSet rs = st.executeQuery(query);
				while (rs.next()) { // List<Concepto> conceptos, Cliente cliente, boolean efectivo, float alicuota,
									// double Subtotal, Date fecha
					aux = new Presupuesto(this.getConceptos(rs.getInt("Nro_Presupuesto")),
							this.getCliente(rs.getInt("Codigo_Cliente")),
							(rs.getString("Efectivo").equals("S") ? true : false), rs.getFloat("Alicuota"),
							rs.getDouble("Subtotal"), rs.getDate("Fecha"), rs.getInt("Mes"));
					aux.actualizarNroPresupuesto(rs.getInt("Nro_Presupuesto"));
					toReturn.add(aux);
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return toReturn;
	}

	/**
	 * método simple que ejecuta una sentencia que elimina todos aquellos
	 * presupuestos no efectivos, solo puede fallar si no hay conexion con la base
	 * de datos. Obs: Si no existes presupuestos en estas condiciones ejecuta una
	 * sentencia SQL que no realiza ningún cambio en la base de datos.
	 */
	public void eliminarPresupuestosNoEfectivos() {
		try {
			String query = "DELETE FROM Presupuesto WHERE Efectivo = 'N'";
			PreparedStatement preparedStmt;
			preparedStmt = conn.prepareStatement(query);
			preparedStmt.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public String fechaUltimoPago(Cliente c) {
		String query = "SELECT Fecha FROM Transaccion WHERE Codigo_cliente=" + c.getCodigoCliente()
				+ " AND Evento='C' ORDER BY Fecha DESC LIMIT 1;";
		Statement st;

		try {
			st = conn.createStatement();
			ResultSet rs = st.executeQuery(query);
			if (rs.next()) {
				SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
				return format1.format(rs.getDate("Fecha"));
			} else {
				System.out.println("[WARNING] Se pidió fechaUltimoPago para cliente que no tiene ninguna fecha.");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;

	}

	/**
	 * Método que elimina un presupuesto borrador (no efectivo) que pudo haber sido
	 * realizado por error por el usuario. Falla si no hay conexión con la base de
	 * datos, o si el presupuesto pasado por parámetro no existo, o si el
	 * presupuesto ya ha sido efectivizado a cuentas corrientes.
	 * 
	 * @param p Recibe el objeto presupuesto a ser borrado, utiliza su nro de
	 *          presupuesto para buscarlo y eliminarlo de la base de datos.
	 * @throws InvalidBudgetException Lanza excepción si el presupuesto no existe en
	 *                                la base de datos (Nro inválido) o si el
	 *                                presupuesto ya ha sido efectivizado.
	 */
	public void eliminarNoEfectivo(Presupuesto p) throws InvalidBudgetException {
		if (!p.hasValidNumber())
			throw new InvalidBudgetException("El presupuesto no existe en la base de datos");
		if (p.getEfectivo())
			throw new InvalidBudgetException("Para eliminar un presupuesto tiene que ser no efectivo.");
		int nro_presu = p.getNroPresupuesto();
		// * * * ELIMINAMOS EL PRESUPUESTO DE LA TABLA DE PRESUPUESTOS * * *
		try {
			String query = "DELETE FROM Presupuesto WHERE Nro_Presupuesto = ?";
			PreparedStatement preparedStmt;
			preparedStmt = conn.prepareStatement(query);
			preparedStmt.setInt(1, nro_presu);
			preparedStmt.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Método que se encarga de cancelar los efectos de una efectivización: - Resta
	 * el valor de la cuenta corriente. - Crea una nueva transaccion del tipo
	 * CANCELACION (x), y la registra en la base de datos (con fecha actual y
	 * sumando el monto del presupuesto). - Marcamos el presupuesto como no
	 * efectivo, tanto en el objeto como en la base de datos.
	 * 
	 * Obs: el monto del presupuesto se suma, porque al efectivizar se resta. La
	 * suma se corresponde con la operación contraria.
	 * 
	 * @param p El objeto presupuesto a desefectivizar, requiere que sea efectivo y
	 *          que tenga nro de presupuesto.
	 * @throws InvalidBudgetException Si no tiene nro de presupuesto o si el
	 *                                presupuesto no es efectivo.
	 */
	public void desefectivizar(Presupuesto p) throws InvalidBudgetException {
		if ((!p.hasValidNumber()) || !(p.getEfectivo()))
			throw new InvalidBudgetException(
					"Presupuesto no existente en base de datos, o no ha sido efectivizado aún (el el método desefectivizar).");

		int nro_presupuesto = p.getNroPresupuesto();

		double monto_a_restar = p.calcularMontoTotal();

		// Calculamos fecha
		SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
		String fechaActual = format1.format(Calendar.getInstance().getTime());
		double estado_cta_corriente;
		double nuevo_estado_cta_corriente;
		try {
			// * * * ACTUALIZAMOS CUENTA CORRIENTE * * *
			estado_cta_corriente = this.obtenerEstadoCuentaCorriente(p.getCliente());
			nuevo_estado_cta_corriente = estado_cta_corriente + monto_a_restar;
			this.actualizarEstadoCuentaCorriente(p.getCliente(), nuevo_estado_cta_corriente);

			// * * * INSERTAMOS TRANSACCION DE CANCELACION EN TABLA DE TRANSACCIONES * * *
			String query = "INSERT INTO Transaccion "
					+ "(Codigo_cliente, Fecha, Evento, Monto, Concepto, Estado_cuenta_corriente) VALUES (?,?,'X',?,?,?) ;";
			// query = "DELETE FROM Transaccion WHERE Nro_Transaccion = ?";
			PreparedStatement preparedStmt;
			preparedStmt = conn.prepareStatement(query);
			preparedStmt.setInt(1, p.getCliente().getCodigoCliente());
			preparedStmt.setString(2, fechaActual);
			preparedStmt.setDouble(3, monto_a_restar);
			preparedStmt.setString(4, "Cancelación Presupuesto #" + p.getNroPresupuesto());
			preparedStmt.setDouble(5, nuevo_estado_cta_corriente);

			preparedStmt.execute();

			// * * * MARCAMOS PRESUPUESTO COMO NO EFECTIVO. * * *
			query = "UPDATE Presupuesto SET Efectivo = 'N' WHERE Nro_Presupuesto = ? "; // lo hacemos no efectivo
			PreparedStatement pt;
			pt = conn.prepareStatement(query);
			pt.setInt(1, nro_presupuesto);
			pt.execute();
			// * * * MARCAMOS EL OBJETO PRESUPUESTO COMO NO EFECTIVO * * *
			p.setEfectivo(false);
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (InvalidClientException e) {
			// No debería entrar acá
			e.printStackTrace();
		}

	}

	/**
	 * Método que recibe un nro_presupuesto y consulta a la base de datos por el
	 * presupuesto que se corresponde con dicho numero. Como el número es único en
	 * la base de datos, solo retorna uno, o ninguno (null) si no existe tal
	 * presupuesto.
	 * 
	 * @param nro_presupuesto Número de presupuesto buscado.
	 * @return Objeto tipo Presupuesto conteniendo todos los datos recuperados de la
	 *         base de datos sobre el presupuesto 'Nro_Presupuestpo', o NULL si no
	 *         existe dicho prespuesto en la base de datos.
	 */
	public Presupuesto verPresupuesto(int nro_presupuesto) {
		Presupuesto toReturn = null;

		String query = "SELECT * FROM Presupuesto WHERE Nro_Presupuesto = " + nro_presupuesto;
		Statement st;

		try {
			st = conn.createStatement();
			ResultSet rs = st.executeQuery(query);
			if (rs.next()) { // List<Concepto> conceptos, Cliente cliente, boolean efectivo, float alicuota,
								// double Subtotal, Date fecha
				toReturn = new Presupuesto(this.getConceptos(nro_presupuesto),
						this.getCliente(rs.getInt("Codigo_Cliente")),
						(rs.getString("Efectivo").equals("S") ? true : false), rs.getFloat("Alicuota"),
						rs.getDouble("Subtotal"), rs.getDate("Fecha"), rs.getInt("Mes"));
				toReturn.actualizarNroPresupuesto(nro_presupuesto);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return toReturn;
	}

	/**
	 * Método que busca en la tabla de Concepto_presupuesto para recuperar todos los
	 * conceptos asociados a un dado presupuesto.
	 * 
	 * @param Nro_presu El nro de presupuesto del cuál se buscan los conceptos.
	 * @return Retorna una lista de conceptos correspondientes al nro buscado, si
	 *         ningún presupuesto coincide retorna una lista vacía.
	 */
	private List<Concepto> getConceptos(int Nro_presu) {
		List<Concepto> lista = new ArrayList<Concepto>();
		Concepto aux;

		String query = "SELECT * FROM Concepto_presupuesto WHERE Nro_Presupuesto = " + Nro_presu;
		Statement st;
		try {
			st = conn.createStatement();
			ResultSet rs = st.executeQuery(query);
			while (rs.next()) {
				aux = new Concepto(rs.getString("Concepto"), rs.getDouble("Monto"));
				lista.add(aux);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return lista;
	}

	/**
	 * Retorna una lista de todos los prespuestos existentes en la base de datos que
	 * fueron realizados al cliente 'cliente'.
	 * 
	 * @param cliente Cliente al cual se le quieren pedir todos los presupuestos. La
	 *                busqueda se realiza mediante su código de cliente.
	 * @return Una lista con todos los presupuestos que le corresponden al cliente
	 *         'cliente', o una lista vacía en caso de que no existe el cliente, o
	 *         no tenga presupuestos asociados.
	 * @throws InvalidClientException En el caso de que no tenga un código de
	 *                                cliente válido (probablemente no sea efectivo
	 *                                en la base de datos si no tiene código).
	 */
	public List<Presupuesto> verPresupuestos(Cliente cliente) throws InvalidClientException {
		if (!cliente.esValidoCodigoCliente())
			throw new InvalidClientException("Cliente no insertado en la base de datos.");

		List<Presupuesto> lista = new ArrayList<Presupuesto>();
		Presupuesto aux;
		Statement st;

		String query = "SELECT * FROM Presupuesto WHERE Codigo_Cliente = " + cliente.getCodigoCliente()
				+ " ORDER BY Fecha ASC";

		try {
			st = conn.createStatement();
			ResultSet rs = st.executeQuery(query);
			while (rs.next()) { // List<Concepto> conceptos, Cliente cliente, boolean efectivo, float alicuota,
								// double Subtotal, Date fecha
				aux = new Presupuesto(this.getConceptos(rs.getInt("Nro_Presupuesto")),
						this.getCliente(rs.getInt("Codigo_Cliente")),
						(rs.getString("Efectivo").equals("S") ? true : false), rs.getFloat("Alicuota"),
						rs.getDouble("Subtotal"), rs.getDate("Fecha"), rs.getInt("Mes"));
				aux.actualizarNroPresupuesto(rs.getInt("Nro_Presupuesto"));
				lista.add(aux);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return lista;
	}

	/**
	 * Método que recibe la denominación de un cliente y recupera todos sus
	 * presupuestos no efectivos.
	 * 
	 * @param denom La denominación a buscar, busca por match en cualquier lugar del
	 *              campo denominación. Si el String es vacío hace match con toda la
	 *              base de datos de presupuestos no efectivos.
	 * @return Lista con los presupuestos no efectivos que coinciden con esa
	 *         búsqueda, lista vacía si nada coincide. Obs: retorna lista vacía si
	 *         el string denom es null e imprime un WARNING.
	 */
	public List<Presupuesto> verPresupuestosNoEfectivosPorDenominacion(String denom) {

		List<Presupuesto> lista = new ArrayList<Presupuesto>();
		if (denom != null) {
			Presupuesto aux;
			Statement st;

			String query = "SELECT * FROM Presupuesto INNER JOIN Cliente ON Presupuesto.Codigo_cliente = Cliente.Codigo_cliente WHERE Cliente.Denominacion LIKE '%"
					+ denom + "%' AND Presupuesto.Efectivo = 'N' ORDER BY Cliente.Denominacion ASC";

			try {
				st = conn.createStatement();
				ResultSet rs = st.executeQuery(query);
				while (rs.next()) { // List<Concepto> conceptos, Cliente cliente, boolean efectivo, float alicuota,
									// double Subtotal, Date fecha
					aux = new Presupuesto(this.getConceptos(rs.getInt("Nro_Presupuesto")),
							this.getCliente(rs.getInt("Codigo_Cliente")),
							(rs.getString("Efectivo").equals("S") ? true : false), rs.getFloat("Alicuota"),
							rs.getDouble("Subtotal"), rs.getDate("Fecha"), rs.getInt("Mes"));
					aux.actualizarNroPresupuesto(rs.getInt("Nro_Presupuesto"));
					lista.add(aux);
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else
			System.out.println("[WARNING] String denom null en método verPresupuestosNoEfectivosPorDenominacion.");
		return lista;
	}

	/**
	 * Método que busca presupuestos no efectivos por cuit y los retorna.
	 * 
	 * @param cuit Cuit a buscar en la base de datos, hace match en cualquier lugar
	 *             del campo Cuit (puede ser al ppio. o al final). Si el String es
	 *             vacío retorna toda la base de datos de no efectivos.
	 * @return Retorna la lista que coincida con los criterios de búsqueda, puede
	 *         ser lista vacía si nada coincide. Obs: retorna también lista vacía si
	 *         el parametro cuit es nulo y imprime un WARNING.
	 */
	public List<Presupuesto> verPresupuestosNoEfectivosPorCuit(String cuit) {

		List<Presupuesto> lista = new ArrayList<Presupuesto>();
		if (cuit != null) {
			Presupuesto aux;
			Statement st;

			String query = "SELECT * FROM Presupuesto INNER JOIN Cliente ON Presupuesto.Codigo_cliente = Cliente.Codigo_cliente WHERE Cliente.CUIT LIKE '%"
					+ cuit + "%' AND Presupuesto.Efectivo = 'N' ORDER BY Cliente.Denominacion ASC";

			try {
				st = conn.createStatement();
				ResultSet rs = st.executeQuery(query);
				while (rs.next()) { // List<Concepto> conceptos, Cliente cliente, boolean efectivo, float alicuota,
									// double Subtotal, Date fecha
					aux = new Presupuesto(this.getConceptos(rs.getInt("Nro_Presupuesto")),
							this.getCliente(rs.getInt("Codigo_Cliente")),
							(rs.getString("Efectivo").equals("S") ? true : false), rs.getFloat("Alicuota"),
							rs.getDouble("Subtotal"), rs.getDate("Fecha"), rs.getInt("Mes"));
					aux.actualizarNroPresupuesto(rs.getInt("Nro_Presupuesto"));
					lista.add(aux);
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else
			System.out.println("[WARNING] campo cuit null (método verPresupuestosNoEfectivosPorCuit.");
		return lista;
	}

	/**
	 * Retorna el último presupuesto que se le realizó al cliente 'cliente'.
	 * 
	 * @param cliente Cliente al cual se le requiere el último presupuesto. Se
	 *                utiliza su código de cliente para la busqueda.
	 * @return El ultimo presupuesto del cliente pasado por parámetro, o nulo en
	 *         caso de que no existe el cliente, o no tenga presupuestos asociados.
	 * @throws InvalidClientException Si el cliente no tiene un nro de cliente
	 *                                valido (probablemente porque no exista en la
	 *                                base de datos) o si el cliente es null.
	 */
	public Presupuesto verUltimoPresupuesto(Cliente cliente) throws InvalidClientException {
		if (!cliente.esValidoCodigoCliente() || cliente == null)
			throw new InvalidClientException("Cliente no insertado en la base de datos o objeto cliente nulo.");

		String query = "SELECT * " + "FROM Presupuesto " + "WHERE Codigo_Cliente = " + cliente.getCodigoCliente()
				+ " AND " + "Fecha = " + "(SELECT  max(Fecha) " + "FROM  Presupuesto " + "WHERE  Codigo_Cliente = "
				+ cliente.getCodigoCliente() + ")";
		Presupuesto toReturn = null;
		Statement st;

		try {
			st = conn.createStatement();
			ResultSet rs = st.executeQuery(query);
			if (rs.next()) {
				toReturn = new Presupuesto(this.getConceptos(rs.getInt("Nro_Presupuesto")),
						this.getCliente(rs.getInt("Codigo_Cliente")),
						(rs.getString("Efectivo").equals("S") ? true : false), rs.getFloat("Alicuota"),
						rs.getDouble("Subtotal"), rs.getDate("Fecha"), rs.getInt("Mes"));
				toReturn.actualizarNroPresupuesto(rs.getInt("Nro_Presupuesto"));
				toReturn.actualizarNroPresupuesto(rs.getInt("Nro_Presupuesto"));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return toReturn;
	}

	/**
	 * Agrega un nuevo presupuesto a la base de datos. Lo vincula con el cliente que
	 * contiene el presupuesto mediante su Codigo_Cliente. Al insertar en la base de
	 * datos al presupuesto se lo hace con un nuevo Nro_Presupuesto, a este mismo
	 * numero se lo agrega al objeto 'p'. Para que mantenga la referencia de su
	 * propio Nro_Presupuesto.
	 * 
	 * Por último se guarda al Nro_Presupuesto+1 como el proximo numero de
	 * presupuesto que va a salir, para poder ofrecer el servicio de devolver este
	 * numero para la interfaz gráfica de construccion de presupuestos.
	 * 
	 * @param p Presupuesto a agregar en la base de datos
	 * @return True si pudo insertar correctamente en la base de datos. False caso
	 *         contrario. Obs: el método retornara false en caso de que el
	 *         presupuesto pasado por parámetro sea null.
	 */
	public boolean agregarPresupuesto(Presupuesto p) {
		boolean toReturn = false;
		if (p != null) {
			String query = "INSERT INTO Presupuesto (Codigo_Cliente, Fecha, Efectivo, Alicuota, Subtotal,Mes) VALUES ('"
					+ p.getCliente().getCodigoCliente() + "', " + "'" + p.getFecha() + "', " + "'"
					+ (p.getEfectivo() ? "S" : "N") + "', " + "'" + p.getAlicuota() + "', " + "'" + p.getSubtotal()
					+ "', " + "'" + p.getMes() + "'); ";
			PreparedStatement pt;

			try {
				pt = conn.prepareStatement(query);
				pt.execute();
				toReturn = true;
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			if (toReturn) {
				// OBTENIENDO EL Nro_Presupuesto del ultimo presupuesto insertado, para
				// actualizarlo del objeto presupuesto.

				String query_aux = "SELECT LAST_INSERT_ID() AS ultimo_presupuesto";
				Statement st;
				try {
					st = conn.createStatement();
					ResultSet rs = st.executeQuery(query_aux);
					int nro_presu = -1;
					if (rs.next()) {
						nro_presu = rs.getInt("ultimo_presupuesto");
						this.ProximoNumeroPresupuesto = nro_presu + 1;
					}
					p.actualizarNroPresupuesto(nro_presu);
					st.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				// Para insertar los conceptos necesitamos el paso previo inmediato, INSERTAR EL
				// NRO DE PRESUPUESTO EN EL OBJETO 'p'
				try {
					insertarConceptos(p);
				} catch (InvalidBudgetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		} else
			System.out.println("[WARNING] Objeto presupuesto null en método agregarPresupuesto.");
		return toReturn;
	}

	/**
	 * Método que inserta todos los conceptos de un dado presupuesto p en la base de
	 * datos. Obs: CUIDADO, ESTE MÉTODO NO ELIMINA NINGÚN CONCEPTO, SI EL
	 * PRESUPUESTO YA TENIA CONCEPTOS, ESTÉ MÉTODO LE AGREGA MÁS CONCEPTOS. Y el
	 * objeto puede quedar desactualizado si en la base de datos hay presupuestos
	 * que en el objeto no están. >> Este método se utiliza para actualizar un
	 * presupuesto primero se borran todos los conceptos con un método auxiliar y
	 * luego se agregan mediante este metodo todos los conceptos nuevos. <<
	 * 
	 * @param p Presupuesto al cuál se le extraeran los conceptos para insertarlos
	 *          en la base de datos.
	 * @throws InvalidBudgetException En caso de que el presupuesto no tenga numero
	 *                                valido o sea un objeto null.
	 */

	private void insertarConceptos(Presupuesto p) throws InvalidBudgetException {
		if (!p.hasValidNumber() || p == null)
			throw new InvalidBudgetException("Presupuesto no creado en base de datos.");
		String query = "INSERT INTO Concepto_presupuesto (Nro_Presupuesto, Concepto, Monto) VALUES (?,?,?)";

		PreparedStatement pt;

		try {
			pt = conn.prepareStatement(query);
			for (Concepto concepto : p.getConceptos()) {
				pt.setInt(1, p.getNroPresupuesto());
				pt.setString(2, concepto.getConcepto());
				pt.setDouble(3, concepto.getMonto());
				pt.addBatch();
			}
			pt.executeBatch();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * Realiza una operacion de UPDATE sobre la base de datos, cambiando los
	 * atributos del presupuesto. Para buscar el presupuesto utiliza el nro de
	 * presupuesto el cual es único. El presupuesto se puede modificar SI SOLO SI el
	 * presupuesto no es efectivo.
	 * 
	 * @param p Presupuesto buscado en la base de datos para ser actualizado.
	 *          Actualiza todos su atributos: Codigo_Cliente, Fecha, Efectivo,
	 *          Alicuota y Subtotal.
	 * @return True si se pudo modificar correctamente. False en caso contrario.
	 * 
	 * 
	 *         OBS IMPORTANTE: el parametro efectivo no debe ser cambiado a mano, en
	 *         lugar de eso se debe usar el método 'efectivizarPresupuesto'.
	 * @throws InvalidBudgetException Si el presupuesto tiene un numero invalid, o
	 *                                si el presupuesto ya es efectivo.
	 */
	// TODO: limitar la edicion a presupuestos no efectivos
	public boolean editarPresupuesto(Presupuesto p) throws InvalidBudgetException {
		if (!p.hasValidNumber())
			throw new InvalidBudgetException("El presupuesto no existe en la base de datos");
		if (p.getEfectivo())
			throw new InvalidBudgetException("El presupuesto ya es efectivo, no puede ser modificado.");
		// TODO chequear que el presupuesto no sea efectivo!!!
		// ===========================================================================================================================================
		boolean toReturn = false;
		String query = "UPDATE Presupuesto SET Codigo_Cliente = ?, Fecha = ?, Efectivo = ?, Alicuota = ?, Subtotal = ?, Mes=? WHERE Nro_Presupuesto = ?";
		PreparedStatement preparedStmt;
		try {
			preparedStmt = conn.prepareStatement(query);
			preparedStmt.setInt(1, p.getCliente().getCodigoCliente());
			preparedStmt.setString(2, p.getFecha());
			preparedStmt.setString(3, (p.getEfectivo() ? "S" : "N"));
			preparedStmt.setFloat(4, p.getAlicuota());
			preparedStmt.setDouble(5, p.getSubtotal());
			preparedStmt.setInt(6, p.getMes());
			preparedStmt.setInt(7, p.getNroPresupuesto());

			// execute the java preparedstatement
			preparedStmt.executeUpdate();
			toReturn = true;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (toReturn)
			actualizarConceptos(p); // el método ELIMINA TODOS LOS CONCEPTOS del presupuesto, y agrega TODOS los
									// conceptos nuevos que estań en esté objeto presupuesto
		return toReturn;
	}

	/**
	 * El método actualiza los conceptos del presupuesto P. Para ello primero los
	 * elimina a todos los viejos y luego agrega todos los conceptos que tenga el
	 * objeto p (insertarConceptos(p)).
	 * 
	 * @param p El presupuesto al cual actualizarle los conceptos.
	 * @throws InvalidBudgetException Lanza excepcion si el presupesto tiene un
	 *                                numero invalido (probablemente porque no este
	 *                                en la base de datos). O si el objeto p es
	 *                                nulo.
	 */
	private void actualizarConceptos(Presupuesto p) throws InvalidBudgetException {
		if (!p.hasValidNumber() || p == null)
			throw new InvalidBudgetException("Presupuesto no creado en base de datos");
		// borrarlos y cargarlos de nuevo..
		boolean eliminados = false;
		String query = "DELETE FROM Concepto_presupuesto WHERE Nro_Presupuesto = ?";
		PreparedStatement preparedStmt;
		try {
			preparedStmt = conn.prepareStatement(query);
			preparedStmt.setInt(1, p.getNroPresupuesto());

			preparedStmt.execute();
			eliminados = true;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (eliminados) {
			// agregamos
			this.insertarConceptos(p);
		}
	}

	/**
	 * Método inverso al método aplicarAumento, se encarga de aplicar un descuento
	 * inverso al del aumentar con el fin de hacer un revertir aumento. Si uno
	 * ejecuta aplicarAumento(x) y un deshacerAumento(x) vuelve al mismo valor de
	 * conceptos (hace un undo).
	 * 
	 * @param porcentaje El porcentaje el cuál se quiere deshacer, valor numerico
	 *                   entero entre 0 y 100. Si el valor no esta dentro de estos
	 *                   rangos el método no hace nada e imprime un warning.
	 */

	public void deshacerAumentoNoEfectivos(int porcentaje) {
		if (porcentaje >= 0 && porcentaje <= 100) {
			double porcentual = 1.0 + ((double) porcentaje / 100.0);
			porcentual = 1.0 / porcentual;

			String query = "UPDATE Concepto_presupuesto AS c INNER JOIN Presupuesto AS p ON p.Nro_presupuesto=c.Nro_presupuesto SET c.Monto=ROUND(c.Monto*?*20)/20 WHERE p.Efectivo='N';";
			PreparedStatement preparedStmt;
			try {
				preparedStmt = conn.prepareStatement(query);
				preparedStmt.setDouble(1, porcentual);

				// execute the java preparedstatement
				preparedStmt.executeUpdate();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			recalcularSubtotales();
		} else
			System.out.println("[WARNING] porcentaje INVALIDO. Método deshacerAumentoNoEfectivos(porcentaje).");
	}

	private void recalcularSubtotales() {
		PreparedStatement preparedStmt;
		String query = "SELECT p.Nro_presupuesto, ROUND(SUM(cp.Monto)*20)/20 AS Subtotal  "
				+ "FROM Presupuesto as p INNER JOIN Concepto_presupuesto AS cp ON p.Nro_presupuesto=cp.Nro_presupuesto "
				+ "WHERE p.Efectivo='N' " + "GROUP BY p.Nro_presupuesto;";
		Statement st;
		String update = "UPDATE Presupuesto SET Subtotal=? WHERE Nro_presupuesto=?";
		try {
			st = conn.createStatement();
			ResultSet rs = st.executeQuery(query);
			double subtotal;
			int Nro_presupuesto;
			while (rs.next()) {
				subtotal = rs.getDouble("Subtotal");
				Nro_presupuesto = rs.getInt("Nro_presupuesto");

				// ACTUALIZACION DE CADA SUBTOTAL DE CADA PRESUPUESTO

				try {
					preparedStmt = conn.prepareStatement(update);
					preparedStmt.setDouble(1, subtotal);
					preparedStmt.setInt(2, Nro_presupuesto);

					// execute the java preparedstatement
					preparedStmt.executeUpdate();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Actualiza el monto de todos los conceptos para cada presupuesto no efectivo
	 * en la base de datos. La actualización aumenta en 'porcentaje'% el monto, si
	 * el porcentaje es invalido NO realiza cambio en la base de datos e imprime un
	 * cartel de WARNING.
	 * 
	 * @param porcentaje Valor entero entre 0 y 100 que representa un valor de
	 *                   porcentaje 'porcentaje'%.
	 */
	public void aplicarAumentoNoEfectivos(int porcentaje) { // Valor entre 0 y 100 porciento
		if (porcentaje >= 0 && porcentaje <= 100) {
			double porcentual = 1.0 + ((double) porcentaje / 100.0);

			String query = "UPDATE Concepto_presupuesto AS c INNER JOIN Presupuesto AS p ON p.Nro_presupuesto=c.Nro_presupuesto SET c.Monto=ROUND(c.Monto*?*20)/20 WHERE p.Efectivo='N';";
			PreparedStatement preparedStmt;
			try {
				preparedStmt = conn.prepareStatement(query);
				preparedStmt.setDouble(1, porcentual);

				// execute the java preparedstatement
				preparedStmt.executeUpdate();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			recalcularSubtotales();

		} else
			System.out.println("[WARNING] porcentaje INVALIDO. Método aplicarAumentoNoEfectivos(porcentaje).");
	}

	/**
	 * Método que genera PARA TODOS los clientes HABILITADOS, una nueva factura NO
	 * efectivizada, con los mismos conceptos que el presupuesto anterior.
	 */
	public void facturarTodos() {
		// +++++++++++++++++++++++++++++++++ * +++++++++++++++++++++++++++++++++ *
		// +++++++++++++++++++++++++++++++++ * +++++++++++++++++++++++++++++++++ *
		// +++++++++++++++++++++++++++++++++ *
		// lo que hace
		// +++++++++++++++++++++++++++++++++ * +++++++++++++++++++++++++++++++++ *
		// +++++++++++++++++++++++++++++++++ * +++++++++++++++++++++++++++++++++ *
		// +++++++++++++++++++++++++++++++++ *
		/*
		 * PASOS: 1 - Para cada Cliente habilitado hacer: I - Recuperar ultimo
		 * presupuesto (método verUltimoPresupuesto(Cliente cliente) ). II - Crear un
		 * nuevo presupuesto idéntco, pero con un nuevo número de presupuesto, fecha
		 * nueva, efectivo en NO y número de transacción -1 (porq aún no es efectivo)
		 * (ver 'facturarBorrador'). III - Insertar nuevo presupuesto en la base de
		 * datos (método: agregarPresupuesto(Presupuesto p).
		 */
		String query = "SELECT * FROM Cliente WHERE Habilitado = 'S'";
		List<Cliente> clientes = new ArrayList<Cliente>();
		Cliente auxiliar;
		Statement st;
		try {
			st = conn.createStatement();
			ResultSet rs = st.executeQuery(query);
			while (rs.next()) {
				// int Codigo_Cliente, String CUIT, String denominacion, String direccion,
				// String localidad,
				// String telefono, String correoElectronico, String condicionIva, String
				// habilitado
				auxiliar = new Cliente(rs.getInt("Codigo_Cliente"), rs.getString("CUIT"), rs.getString("Denominacion"),
						rs.getString("Direccion"), rs.getString("Localidad"), rs.getString("Telefono"),
						rs.getString("Email"), rs.getString("Condicion_iva"), rs.getString("Habilitado"));
				clientes.add(auxiliar);
			}
			st.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for (Cliente c : clientes) {
			try {
				this.facturarBorrador(c);
			} catch (InvalidClientException e) {// TODO
				e.printStackTrace();
			}
		}

	}

	// HECHO POR VIRI: CAMBIO DE ENCABEZADO
	// private void facturarBorrador(Cliente cliente) throws InvalidClientException{
	public Presupuesto facturarBorrador(Cliente cliente) throws InvalidClientException {
		Presupuesto ultimo = this.verUltimoPresupuesto(cliente);
		// System.out.println(ultimo==null);
		// Calculo para obtener el monto total nuevo (fix para el caso donde el ultimo
		// presupuesto fue realizado con el
		// sistema viejo y el monto total y la suma de los montos da distinto.

		double montoTotal = 0;

		if (ultimo != null) {
			for (Concepto c : ultimo.getConceptos())
				montoTotal += c.getMonto();
		}
		montoTotal = Math.round(montoTotal * 20.0) / 20.0;
		// El presupuesto se guarda con una fecha por default que es la fecha del
		// borrador, pero al efectivizarse se cambia
		// por la fecha de efectivización, la vista no debería mostrar la fecha de un
		// presupuesto borrador porque no es la
		// fecha que quedará, la que queda es la del día de efectivización.

		int nuevo_mes = ultimo == null || ultimo.getMes() == 12 ? 1 : ultimo.getMes() + 1;
		Presupuesto nuevo = new Presupuesto((ultimo == null ? new ArrayList<Concepto>() : ultimo.getConceptos()),
				cliente, false, (ultimo == null ? 0 : ultimo.getAlicuota()), montoTotal,
				Calendar.getInstance().getTime(), nuevo_mes);
		this.agregarPresupuesto(nuevo);
		return nuevo;

	}

	/**
	 * Permite recuperar aquellos presupuestos no efectivos (los borradores), para
	 * poder editarlos uno por uno, modificarlos, guardarlos en la base de datos
	 * nuevamente corregidos, o incluso para efectivizarlos.
	 * 
	 * @return Una lista con todos los presupuestos no efectivos, o una lista vacía
	 *         en caso de que no haya presupuestos sin efectivizar.
	 * 
	 *         Obs: Puede entregar presupuestos de clientes no habilitados, si es
	 *         que existen clientes no habilitados con presupuestos borrador
	 *         creados.
	 */
	public List<Presupuesto> obtenerPresupuestosNoEfectivos() {
		String query = "SELECT * "
				+ "FROM Presupuesto AS p INNER JOIN Cliente AS c ON p.Codigo_Cliente = c.Codigo_Cliente "
				+ "WHERE p.Efectivo = 'N'  ORDER BY c.Denominacion ASC";

		List<Presupuesto> toReturn = new ArrayList<Presupuesto>();
		Presupuesto aux;

		try {
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery(query);
			while (rs.next()) {
				aux = new Presupuesto(this.getConceptos(rs.getInt("Nro_Presupuesto")),
						this.getCliente(rs.getInt("Codigo_Cliente")),
						(rs.getString("Efectivo").equals("S") ? true : false), rs.getFloat("Alicuota"),
						rs.getDouble("Subtotal"), rs.getDate("Fecha"), rs.getInt("Mes"));
				aux.actualizarNroPresupuesto(rs.getInt("Nro_Presupuesto"));
				toReturn.add(aux);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return toReturn;
	}

	// ==================================================================================================================================
	// ** ** ** ** TRANSACCIONES ** ** ** **
	// ==================================================================================================================================

	/**
	 * Método complejo que toma un presupuesto y lo efectiviza realizando los
	 * siguientes pasos: 1. Crea la transaccion asociada a la efectivización, con
	 * fecha actual y con el monto y los valores del presupuesto. 2. Actualiza el
	 * estado de la cuenta corriente restandole el monto del presupuesto (generando
	 * deuda),en la base de datos (métodos auxiliares:
	 * obtenerEstadoCuentaCorriente,actualizarEstadoCuentaCorriente). 3. Se registra
	 * la transacción en la base de datos. 4. Se vincula el presupuesto a el numero
	 * de transaccion que generó. 5. Se cambia el atributo del presupuesto para
	 * reflejar que ya es efectivo. 6. Este último cambio es enviado a la base de
	 * datos y termina el método.
	 * 
	 * @param p
	 * @return Retorna la Transaccion que se generó al efectiviar el presupuesto
	 * @throws InvalidBudgetException
	 */
	public Transaccion efectivizarPresupuesto(Presupuesto p) throws InvalidBudgetException {
		if (!p.hasValidNumber())
			throw new InvalidBudgetException("Presupuesto no creado en base de datos.");

		double montoTotal = p.calcularMontoTotal();
		// proceso de actualizar las cuentas corrientes.
		Transaccion t = null;
		boolean efectivo = false;

		String query = "INSERT INTO Transaccion (Codigo_Cliente, Fecha, Evento, Monto, Concepto, Estado_cuenta_corriente) VALUES (?,?,?,?,?,?) ";
		PreparedStatement preparedStmt;

		try {
			// MODIFICAR CUENTA CORRIENTE
			double estado_cuenta_corriente = 0;
			try {
				estado_cuenta_corriente = this.obtenerEstadoCuentaCorriente(p.getCliente());
			} catch (InvalidClientException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			double nuevo_estado = estado_cuenta_corriente - montoTotal;
			this.actualizarEstadoCuentaCorriente(p.getCliente(), nuevo_estado);
			// REGISTRAR TRANSACCION
			t = new Transaccion(p.getCliente(), Calendar.getInstance().getTime(), 'D', montoTotal, "", nuevo_estado);

			preparedStmt = conn.prepareStatement(query);
			preparedStmt.setInt(1, p.getCliente().getCodigoCliente());
			preparedStmt.setString(2, t.getFecha());
			preparedStmt.setString(3, "P");
			preparedStmt.setDouble(4, montoTotal);
			preparedStmt.setString(5, "Presupuesto #" + p.getNroPresupuesto());
			preparedStmt.setDouble(6, nuevo_estado);

			preparedStmt.execute();

			efectivo = true;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		int nro_trans = -1;
		//
		if (efectivo) {
			// REGISTRAR CAMBIO EN EL OBJETO PRESUPUESTO.
			// 1. AGREGAMOS EL NRO DE TRANSACCION GENERADA AL PRESUPUESTO
			// 2. MARCAMOS PRESUPUESTO COMO EFECTIVO
			// 3. REGISTRAMOS CAMBIO EN EFECTIVO EN LA BASE DE DE DATOS.
			String query_aux = "SELECT LAST_INSERT_ID() AS ultima_transaccion";
			Statement st;
			try {
				st = conn.createStatement();
				ResultSet rs = st.executeQuery(query_aux);
				if (rs.next())
					nro_trans = rs.getInt("ultima_transaccion");
				p.actualizarNroTransaccion(nro_trans);
				st.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			p.setFecha(Calendar.getInstance().getTime()); // Ponemos la fecha en que se efectivizo en el objeto y
															// posteriormente
															// la corregimos en la base de datos
			// HACEMOS EFECTIVO EL PRESU EN LA BASE DE DATOS
			query = "UPDATE Presupuesto SET Efectivo = 'S', Fecha = ?, Nro_Transaccion = ?  WHERE Nro_Presupuesto = ? "; // lo
																															// hacemos
																															// efectivo
			// y colocamos la fecha
			// de efectivizacion y asociamos el presupuesto con la transaccion que lo hizo
			// efectivo.
			PreparedStatement pt;

			try {
				pt = conn.prepareStatement(query);
				pt.setString(1, p.getFecha());
				pt.setInt(2, nro_trans);
				pt.setInt(3, p.getNroPresupuesto());
				pt.execute();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// HACEMOS EFECTIVO EL PRESU EN EL OBJETO

			p.setEfectivo(true);
		}
		return (efectivo ? t : null);// devolvemos la transaccion solo si la logramos guardar en la base de datos
	}

	/**
	 * Recuperar el presupuesto asociado a la transaccion t. Si no existe tal
	 * presupuesto entonces retorna null.
	 * 
	 * @param t La transacción a la cuaĺ se le quiere buscar el presupuesto
	 *          asociado, puede que no exista tal presupuesto.
	 * @return Recupera el objeto Presupuesto asociado (si es que existe) y lo
	 *         retorna.
	 */

	public Presupuesto getPresupuestoAsociado(Transaccion t) {
		Presupuesto toReturn = null;

		String query = "SELECT * FROM Presupuesto WHERE Nro_Transaccion = " + t.getNroTransaccion();
		Statement st;

		try {
			st = conn.createStatement();
			ResultSet rs = st.executeQuery(query);
			if (rs.next()) { // List<Concepto> conceptos, Cliente cliente, boolean efectivo, float alicuota,
								// double Subtotal, Date fecha
				toReturn = new Presupuesto(this.getConceptos(rs.getInt("Nro_Presupuesto")), t.getCliente(),
						(rs.getString("Efectivo").equals("S") ? true : false), rs.getFloat("Alicuota"),
						rs.getDouble("Subtotal"), rs.getDate("Fecha"), rs.getInt("Mes"));
				toReturn.actualizarNroPresupuesto(rs.getInt("Nro_Presupuesto"));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return toReturn;
	}

	/**
	 * Recupera los ultimos movimientos para un dado cliente C.
	 * 
	 * @param C El cliente al cuál se le quiere recuperar TODOS sus ultimos
	 *          movimientos.
	 * @return Una lista de Transacciones conteniendo TODOS los movimientos del
	 *         cliente, o una lista vacía en caso de que no haya movimientos.
	 * 
	 * 
	 *         OBS: Si el cliente es nulo o invalido NO HACE NADA. //TODO
	 */

	public List<Transaccion> ultimosMovimientos(Cliente C) {

		List<Transaccion> transacciones = new ArrayList<Transaccion>();

		if (!C.esValidoCodigoCliente() || C == null)
			System.out.println("[WARNING] cliente invalido o nulo en ultimosMovimientos");
		else {
			Transaccion transaccion_aux;
			String query = "SELECT * FROM Transaccion WHERE Codigo_Cliente = " + C.getCodigoCliente()
					+ " ORDER BY Nro_Transaccion";

			Statement st;
			try {
				st = conn.createStatement();
				ResultSet rs = st.executeQuery(query);
				while (rs.next()) {
					// int Codigo_Cliente, String CUIT, String denominacion, String direccion,
					// String localidad,
					// String telefono, String correoElectronico, String condicionIva, String
					// habilitado
					transaccion_aux = new Transaccion(C, rs.getDate("Fecha"), rs.getString("Evento").charAt(0),
							rs.getDouble("Monto"), rs.getString("Concepto"), rs.getDouble("Estado_cuenta_corriente")

					);
					transaccion_aux.actualizarNroTransaccion(rs.getInt("Nro_Transaccion"));
					transacciones.add(transaccion_aux);
				}
				st.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return transacciones;
	}

	public List<Transaccion> ultimosMovimientosDesdeHasta(Cliente C, String desde, String hasta) {

		// CALCULAMOS PRIMER Y ULTIMA TRANSACCION =========
		String query = "SELECT Nro_Transaccion FROM Transaccion WHERE Codigo_Cliente = " + C.getCodigoCliente()
				+ " AND Fecha<='" + hasta + "' AND Fecha>='" + desde + "' ORDER BY Nro_Transaccion";
		int primera_Trans = -1;
		int ultima_Trans = -1;
		Statement st;
		try {
			st = conn.createStatement();
			ResultSet rs = st.executeQuery(query);
			while (rs.next()) {
				// int Codigo_Cliente, String CUIT, String denominacion, String direccion,
				// String localidad,
				// String telefono, String correoElectronico, String condicionIva, String
				// habilitado
				if (primera_Trans == -1)
					primera_Trans = rs.getInt("Nro_Transaccion");
				ultima_Trans = rs.getInt("Nro_Transaccion");

			}
			st.close();
		} catch (SQLException e) {
		}
		// ==============================================
		List<Transaccion> transacciones = new ArrayList<Transaccion>();

		if (!C.esValidoCodigoCliente() || C == null)
			System.out.println("[WARNING] cliente invalido o nulo en ultimosMovimientos");
		else {
			Transaccion transaccion_aux;
			if (primera_Trans != -1)
				query = "SELECT * FROM Transaccion WHERE Codigo_Cliente = " + C.getCodigoCliente()
						+ " AND Nro_Transaccion<='" + ultima_Trans + "' AND Nro_Transaccion>='" + primera_Trans
						+ "' ORDER BY Nro_Transaccion";
			else
				query = "SELECT * FROM Transaccion WHERE Codigo_Cliente = " + C.getCodigoCliente() + " AND Fecha<='"
						+ hasta + "' AND Fecha>='" + desde + "' ORDER BY Nro_Transaccion";

			try {
				st = conn.createStatement();
				ResultSet rs = st.executeQuery(query);
				while (rs.next()) {
					// int Codigo_Cliente, String CUIT, String denominacion, String direccion,
					// String localidad,
					// String telefono, String correoElectronico, String condicionIva, String
					// habilitado
					transaccion_aux = new Transaccion(C, rs.getDate("Fecha"), rs.getString("Evento").charAt(0),
							rs.getDouble("Monto"), rs.getString("Concepto"), rs.getDouble("Estado_cuenta_corriente")

					);
					transaccion_aux.actualizarNroTransaccion(rs.getInt("Nro_Transaccion"));
					transacciones.add(transaccion_aux);
				}
				st.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return transacciones;
	}

	/**
	 * Una lista con las cuentas corrientes de todos los clientes habilitados.
	 * 
	 * @return Una lista con objetos CuentaCorriente, donde cada objeto tiene el
	 *         Cliente en cuestion, y el monto (double) que representa el estado de
	 *         la cuenta corriente.
	 */
	public List<CuentaCorriente> getCuentasCorrientesHabilitadosSinCeros() {

		Cliente aux;
		double monto_aux;
		List<CuentaCorriente> lista = new ArrayList<CuentaCorriente>();
		String query = "SELECT * "
				+ "FROM Cliente AS C INNER JOIN Cuenta_corriente AS CC ON CC.Codigo_cliente= C.Codigo_cliente WHERE C.Habilitado= 'S' AND CC.Monto!=0 ORDER BY C.Denominacion";

		Statement st;
		try {
			st = conn.createStatement();
			ResultSet rs = st.executeQuery(query);
			while (rs.next()) {
				// int Codigo_Cliente, String CUIT, String denominacion, String direccion,
				// String localidad,
				// String telefono, String correoElectronico, String condicionIva, String
				// habilitado
				aux = new Cliente(rs.getInt("Codigo_Cliente"), rs.getString("CUIT"), rs.getString("Denominacion"),
						rs.getString("Direccion"), rs.getString("Localidad"), rs.getString("Telefono"),
						rs.getString("Email"), rs.getString("Condicion_iva"), rs.getString("Habilitado"));
				monto_aux = rs.getDouble("Monto");
				lista.add(new CuentaCorriente(aux, monto_aux));
			}
			st.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return lista;

	}

	/**
	 * Una lista con las cuentas corrientes de todos los clientes habilitados.
	 * 
	 * @return Una lista con objetos CuentaCorriente, donde cada objeto tiene el
	 *         Cliente en cuestion, y el monto (double) que representa el estado de
	 *         la cuenta corriente.
	 */
	public List<CuentaCorriente> getCuentasCorrientesHabilitados() {

		Cliente aux;
		double monto_aux;
		List<CuentaCorriente> lista = new ArrayList<CuentaCorriente>();
		String query = "SELECT * "
				+ "FROM Cliente AS C INNER JOIN Cuenta_corriente AS CC ON CC.Codigo_cliente= C.Codigo_cliente WHERE C.Habilitado= 'S' ORDER BY C.Denominacion ASC";

		Statement st;
		try {
			st = conn.createStatement();
			ResultSet rs = st.executeQuery(query);
			while (rs.next()) {
				// int Codigo_Cliente, String CUIT, String denominacion, String direccion,
				// String localidad,
				// String telefono, String correoElectronico, String condicionIva, String
				// habilitado
				aux = new Cliente(rs.getInt("Codigo_Cliente"), rs.getString("CUIT"), rs.getString("Denominacion"),
						rs.getString("Direccion"), rs.getString("Localidad"), rs.getString("Telefono"),
						rs.getString("Email"), rs.getString("Condicion_iva"), rs.getString("Habilitado"));
				monto_aux = rs.getDouble("Monto");
				lista.add(new CuentaCorriente(aux, monto_aux));
			}
			st.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return lista;

	}

	/**
	 * Método privado para obtener el double que representa el estado de la cuenta
	 * corriente del cliente C pasado por parámetro.
	 * 
	 * @param C El cliente al cuál se le quiere recuperar el estado de la cuenta
	 *          corriente
	 * @return Un double que representa el estado actual de la cuenta corriente
	 * @throws InvalidClientException Si el código de cliente es invalido (no existe
	 *                                el cliente en la base de datos), o si el
	 *                                cliente es null.
	 * 
	 */
	private double obtenerEstadoCuentaCorriente(Cliente C) throws InvalidClientException {
		if (!C.esValidoCodigoCliente() || C == null)
			throw new InvalidClientException("Cliente no insertado en la base de datos.");

		String query = "SELECT * FROM Cuenta_corriente WHERE Codigo_Cliente = " + C.getCodigoCliente();
		double estado = -1;
		Statement st;
		try {
			st = conn.createStatement();
			ResultSet rs = st.executeQuery(query);
			if (rs.next()) {
				// int Codigo_Cliente, String CUIT, String denominacion, String direccion,
				// String localidad,
				// String telefono, String correoElectronico, String condicionIva, String
				// habilitado
				estado = rs.getDouble("Monto");
			}
			st.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return estado;
	}

	/**
	 * Método que permite modificar el double que representa el estado de la cuenta
	 * corriente del cliente C.
	 * 
	 * @param c     El Cliente al cuál modificarle la cuenta corriente.
	 * @param monto El valor nuevo para almacenar en la base de datos.
	 * 
	 *              OBS: Si el cliente es nulo o invalido NO HACE NADA. //TODO
	 */
	private void actualizarEstadoCuentaCorriente(Cliente c, double monto) {
		if (!c.esValidoCodigoCliente() || c == null)
			System.out.println("[WARNING] Cliente invalido o nulo en actualizarEstadoCuentaCorriente");
		else {
			String query = "UPDATE Cuenta_corriente SET Codigo_Cliente=?,  Monto = ?  WHERE Codigo_Cliente=? ";
			PreparedStatement pt;

			try {
				pt = conn.prepareStatement(query);
				pt.setInt(1, c.getCodigoCliente());
				pt.setDouble(2, monto);
				pt.setInt(3, c.getCodigoCliente());
				pt.execute();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * Método que permite reflejar la situación en la que un cliente realizo un
	 * pago. Es un pago de un importe de 'monto_pagado' en la cuenta del cliente
	 * 'cliente'. Con la posibilidad de dejar una observación mediante el String
	 * "obs".
	 * 
	 * PASOS: 1. genera el cambio en la cuenta corriente 2. genera la transaccion.
	 * 3. guarda la transaccion en la base de datos.
	 * 
	 * @param cliente      Cliente que efectúa el pago.
	 * @param monto_pagado importe del monto pagado.
	 * @param obs          Observación sobre el pago.
	 * @return Retorna la transacción que genero en la base de datos. Returna null
	 *         si hay algun problema al insertar en la base de datos.
	 * @throws InvalidClientException
	 */
	public Transaccion efectuarPago(Cliente cliente, double monto_pagado, String obs) throws InvalidClientException {
		if (!cliente.esValidoCodigoCliente())
			throw new InvalidClientException("Cliente no insertado en la base de datos.");

		Transaccion toReturn = null;
		boolean efectivo = false;
		String query = "INSERT INTO Transaccion (Codigo_Cliente, Fecha, Evento, Monto, Concepto, Estado_cuenta_corriente) VALUES (?,?,?,?,?,?) ";
		PreparedStatement preparedStmt;
		try {

			// MODIFICAR CUENTA CORRIENTE
			double estado_cuenta_corriente = this.obtenerEstadoCuentaCorriente(cliente);
			double nuevo_estado = estado_cuenta_corriente + monto_pagado;
			// APLICAR REDONDEO (?)
			nuevo_estado = Math.round(nuevo_estado * 20.0) / 20.0;
			// ACTUALIZAR ESTADO DE CUENTA CORRIENTE
			this.actualizarEstadoCuentaCorriente(cliente, nuevo_estado);
			// REGISTRAR TRANSACCION
			toReturn = new Transaccion(cliente, Calendar.getInstance().getTime(), 'C', monto_pagado, obs, nuevo_estado);

			preparedStmt = conn.prepareStatement(query);
			preparedStmt.setInt(1, cliente.getCodigoCliente());
			preparedStmt.setString(2, toReturn.getFecha());
			preparedStmt.setString(3, "C");
			preparedStmt.setDouble(4, monto_pagado);
			preparedStmt.setString(5, obs);
			preparedStmt.setDouble(6, nuevo_estado);

			preparedStmt.execute();
			efectivo = true; // llego sin excepcion hasta aquí

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return (efectivo ? toReturn : null);// devolvemos la transaccion solo si la logramos guardar en la base de datos
	}

}
