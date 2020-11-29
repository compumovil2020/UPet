import 'package:firebase_core/firebase_core.dart';
import 'package:flutter/material.dart';

import 'Vistas/listaMensajes.dart';

void main() async {
  WidgetsFlutterBinding.ensureInitialized();
  await Firebase.initializeApp();
  runApp(MyApp());
}

class MyApp extends StatelessWidget {

  int hexColor(String colorHexcode) {
    String colornew = "0xff" + colorHexcode;
    colornew = colornew.replaceAll("#", '');
    int colorint = int.parse(colornew);
    return colorint;
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Chat',
      theme: ThemeData(
        scaffoldBackgroundColor: Color(hexColor("#F8F3D4")),
        primaryColor: Color(hexColor("#00B8A9")),
      ),
      home: listaMensajes(),
      debugShowCheckedModeBanner: false,
    );
  }

}
