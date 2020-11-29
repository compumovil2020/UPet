import 'dart:developer';

import 'package:firebase_auth/firebase_auth.dart';
import 'package:flutter/material.dart';
import 'package:cloud_firestore/cloud_firestore.dart';
import 'package:flutter_module/Modelo/Usuario.dart';

class listaMensajes extends StatefulWidget {
  @override
  _listaMensajesState createState() => _listaMensajesState();
}

class _listaMensajesState extends State<listaMensajes> {
  User usuarioActual;
  FirebaseFirestore firestore;
  Usuario usuario;
  Query query;

  @override
  void initState() {
    // TODO: implement initState
    super.initState();
    usuarioActual = FirebaseAuth.instance.currentUser;
    firestore = FirebaseFirestore.instance;
    constructorQuery();
  }

  definirTipoUsuario() async{
    firestore.collection('usuarios').doc(usuarioActual.uid).get().then((DocumentSnapshot documentSnapshot) {
      if (documentSnapshot.exists) {
        usuario = new Usuario.fromMap(documentSnapshot.data());
      }else{
        print('No existe el usuario');
      }
    });
  }

  constructorQuery() async{
    definirTipoUsuario();
    if(usuario.tipoUsuario == "Normal"){
      print("ENTRO PRIMERO");
      query = firestore.collection('usuarios').where('tipoUsuario',isEqualTo: 'Paseador');
    }else{
      print("ENTRO SEGUNDO");
      query = firestore.collection('usuarios').where('tipoUsuario',isEqualTo: 'Normal');
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text("Upet"),),
      body: StreamBuilder<QuerySnapshot>(
        stream:   firestore.collection('usuarios').snapshots(),
        builder: (BuildContext context, AsyncSnapshot<QuerySnapshot> snapshot) {
          if (snapshot.hasError) {
            return Text('Error al cargar');
          }

          if (snapshot.connectionState == ConnectionState.waiting) {
            return Text("Cargando...");
          }

          return new ListView(
            children: snapshot.data.docs.map((DocumentSnapshot document) {
              return new ListTile(
                title: new Text(document.data()['nombre']),
                subtitle: new Text(document.data()['apellido']),
              );
            }).toList(),
          );
        },
      )
    );
  }

}
