class Usuario{
  String apellido;
  String barrio;
  String celular;
  String ciudad;
  String direccion;
  String nombre;
  String tipoUsuario;
  String usuario;

  Usuario.fromMap(Map<String,dynamic> data){
    apellido = data['apellido'];
    barrio = data['barrio'];
    celular = data['celular'];
    ciudad = data['ciudad'];
    direccion = data['direccion'];
    nombre = data['nombre'];
    tipoUsuario = data['tipoUsuario'];
    usuario = data['usuario'];
  }
}