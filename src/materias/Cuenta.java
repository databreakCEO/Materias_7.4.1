/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package materias;

import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Frodo
 */
public class Cuenta extends javax.swing.JFrame {

    /**
     * Creates new form Cuenta
     */
    DefaultListModel listaTotal, listaPendientes;
    MateriasFrame mf;
    String nombre;
    String carrera;
    Archivo a1;
    Archivo kardex;
    Archivo matCursando;
    DefaultListModel auxiliar = new DefaultListModel();
    Object carreraID = 0;
    File carpeta;
    String[] listado;
    String directorio;
    boolean cargando = true;
    Conexiones con;
    String pass;
    int creditos;
    File direct;

    public Cuenta(String carrera, String nombre, String cuenta, String pass) {
        initComponents();
        try {
            try {
                this.setIconImage(new ImageIcon(getClass().getResource("/Imagenes/rayo.jpg")).getImage());
            } catch (Exception e) {

            }

//        jLabImagenFondo.setSize(MAXIMIZED_HORIZ, MAXIMIZED_VERT);
            this.setTitle("Cuenta");
            con = new Conexiones("ITLDB", "root", "", "localhost");
            this.pass = pass;

            this.setLocationRelativeTo(null);
            this.nombre = nombre;
            this.carrera = carrera;
            refresh();
        } catch (Exception e) {

        }
        carpeta = new File("Materias por carrera");
        listado = carpeta.list();
    }

    public void refresh() {
        String[] alu = new String[8];
        Archivo al = new Archivo(nombre + "\\datos.txt");
        System.out.println(al.path.getAbsolutePath());
        al.crearLectura();
        for (int i = 0; i < 8; i++) {
            alu[i] = al.LeerLinea();
        }
        int creditos=0, creditAcum=0;
        creditos = Integer.valueOf(al.LeerLinea());
        creditAcum = Integer.valueOf(al.LeerLinea());
        al.CerrarLectura();
        Alumno.Alumno(alu[0], alu[1], alu[2], alu[3],
                alu[4], alu[5], alu[6], alu[7],
                creditos, creditAcum);
        listaTotal = new DefaultListModel();
        listaPendientes = new DefaultListModel();
//        String direccion = "";
        a1 = new Archivo(nombre + "\\materiasEstado.txt");
        kardex = new Archivo(nombre + "\\kardex.txt");
        matCursando = new Archivo(nombre + "\\materiasCursando.txt");
        a1.crearLectura();
        kardex.crearLectura();
        matCursando.crearLectura();
        jLabCarrera.setText(carrera);
        jLabCredit.setText(Alumno.creditos + "");
        jLabAct.setText("Activo: " + Alumno.activo.toUpperCase());
        jLabCuenta.setText(Alumno.numControl + " - " + Alumno.apPat + " " + Alumno.apMat + " " + Alumno.nombres);
//        jSpinner1.setValue(1);
        int con = 0;
        String line = a1.LeerLinea();
        listaTotal.addElement(line);
        boolean band = true;
        while (line != null) {
            band = true;
            for (int i = 0; i < listaTotal.size(); i++) {
                if (line.equals(listaTotal.get(i)) || line.equals("RESIDENCIA")) {
                    band = false;
                }
            }
            if (band == true) {
                listaTotal.addElement(line);
                con++;
            }
            line = a1.LeerLinea();
        }

//            listaAprobadas.addElement(line);
        a1.CerrarLectura();
        auxiliar = listaTotal;
        DefaultTableModel modelo = (DefaultTableModel) jTable1.getModel();
        for (int i = 0; i < listaTotal.size(); i++) {
            modelo.setRowCount(i + 1);
            String cadena = listaTotal.get(i).toString();
            jTable1.setValueAt(cadena, i, 0);
        }

        String linea = kardex.LeerLinea();
        String[] datosKardex;

        DefaultTableModel modeloKar = (DefaultTableModel) jTableKardex.getModel();
        modeloKar.setRowCount(0);
        modeloKar.setColumnCount(0);

        if (linea != null) {
            modeloKar.addColumn("Clave");
            modeloKar.addColumn("Materia");
            modeloKar.addColumn("Calificación");
            modeloKar.addColumn("Semestre");
            jTableKardex.getColumnModel().getColumn(0).setPreferredWidth(10);
            jTableKardex.getColumnModel().getColumn(1).setPreferredWidth(400);
            jTableKardex.getColumnModel().getColumn(2).setPreferredWidth(10);
            jTableKardex.getColumnModel().getColumn(3).setPreferredWidth(5);

            while (linea != null) {
                datosKardex = linea.split(" - ");
                modeloKar.addRow(datosKardex);
                linea = kardex.LeerLinea();
                listaPendientes.addElement(datosKardex[1]);
            }
        }
        kardex.CerrarLectura();

        DefaultTableModel modeloCursando = (DefaultTableModel) jTableMateriasCursando.getModel();
        modeloCursando.setRowCount(0);
        modeloCursando.setColumnCount(0);
        try {
            linea = matCursando.LeerLinea();
            if (linea != null) {
                String[] datosCursando;
                modeloCursando.addColumn("Clave");
                modeloCursando.addColumn("Materia");
                modeloCursando.addColumn("Docente");
                modeloCursando.addColumn("Calificación");
                jTableMateriasCursando.getColumnModel().getColumn(0).setPreferredWidth(5);
                jTableMateriasCursando.getColumnModel().getColumn(0);
                jTableMateriasCursando.getColumnModel().getColumn(1).setPreferredWidth(215);
                jTableMateriasCursando.getColumnModel().getColumn(2).setPreferredWidth(215);
                jTableMateriasCursando.getColumnModel().getColumn(3).setPreferredWidth(5);
                while (linea != null) {
                    datosCursando = linea.split(" - ");
                    modeloCursando.addRow(datosCursando);
                    linea = matCursando.LeerLinea();
                }
            } else {
                modeloCursando.addColumn("Sin Materias Cargadas");
                modeloCursando.addRow(new Object[]{"Aún no ha seleccionado su carga académica."});
            }
        } catch (Exception e) {

        }
        kardex.CerrarLectura();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jButCrearHorarios = new javax.swing.JButton();
        jButActMaterias = new javax.swing.JButton();
        jButActDatos = new javax.swing.JButton();
        jLabCuenta = new javax.swing.JLabel();
        jLabAct = new javax.swing.JLabel();
        jButVerHorarios = new javax.swing.JButton();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTableKardex = new javax.swing.JTable();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTableMateriasCursando = new javax.swing.JTable();
        jLabCarrera = new javax.swing.JLabel();
        jLabCredit = new javax.swing.JLabel();
        jLabImagen = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                formWindowClosed(evt);
            }
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jButCrearHorarios.setBackground(new java.awt.Color(255, 255, 255));
        jButCrearHorarios.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jButCrearHorarios.setText("Crear horarios");
        jButCrearHorarios.setToolTipText("Oprima para empezar a crear sus pre-horarios");
        jButCrearHorarios.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButCrearHorariosActionPerformed(evt);
            }
        });
        getContentPane().add(jButCrearHorarios, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 110, 150, -1));

        jButActMaterias.setBackground(new java.awt.Color(255, 255, 255));
        jButActMaterias.setText("Actualizar horarios de materias");
        jButActMaterias.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButActMateriasActionPerformed(evt);
            }
        });
        getContentPane().add(jButActMaterias, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 70, -1, -1));

        jButActDatos.setBackground(new java.awt.Color(255, 255, 255));
        jButActDatos.setText("Actualizar sus datos");
        jButActDatos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButActDatosActionPerformed(evt);
            }
        });
        getContentPane().add(jButActDatos, new org.netbeans.lib.awtextra.AbsoluteConstraints(299, 70, 150, -1));

        jLabCuenta.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabCuenta.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        getContentPane().add(jLabCuenta, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 40, 550, 28));

        jLabAct.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabAct.setText("Activo: ");
        getContentPane().add(jLabAct, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 0, 140, 30));

        jButVerHorarios.setBackground(new java.awt.Color(255, 255, 255));
        jButVerHorarios.setText("Ver horarios guardados");
        jButVerHorarios.setToolTipText("Oprima para visualizar los horarios guardados.");
        jButVerHorarios.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButVerHorariosActionPerformed(evt);
            }
        });
        getContentPane().add(jButVerHorarios, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 110, 190, -1));

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Nombre de materia"
            }
        ));
        jTable1.getTableHeader().setReorderingAllowed(false);
        jScrollPane3.setViewportView(jTable1);
        if (jTable1.getColumnModel().getColumnCount() > 0) {
            jTable1.getColumnModel().getColumn(0).setPreferredWidth(1000);
        }

        jTabbedPane1.addTab("Materias para horarios", jScrollPane3);

        jTableKardex.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Clave", "Nombre", "Calificacion", "Semestre"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, true, true
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(jTableKardex);

        jTabbedPane1.addTab("Kardex", jScrollPane1);

        jTableMateriasCursando.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"Aún no ha seleccionado su carga académica."}
            },
            new String [] {
                "Sin Materias Cargadas"
            }
        ));
        jScrollPane2.setViewportView(jTableMateriasCursando);

        jTabbedPane1.addTab("Materias Cursando", jScrollPane2);

        getContentPane().add(jTabbedPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 140, 580, 480));

        jLabCarrera.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        getContentPane().add(jLabCarrera, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, 220, 30));

        jLabCredit.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabCredit.setText("Creditos: ");
        getContentPane().add(jLabCredit, new org.netbeans.lib.awtextra.AbsoluteConstraints(390, 0, 150, 30));

        jLabImagen.setBackground(new java.awt.Color(255, 255, 255));
        jLabImagen.setOpaque(true);
        getContentPane().add(jLabImagen, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 600, 630));

        pack();
    }// </editor-fold>//GEN-END:initComponents


    private void jButCrearHorariosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButCrearHorariosActionPerformed
        // TODO add your handling code here:
        try {
            mf = new MateriasFrame(listaPendientes, carrera, nombre);
            mf.setVisible(true);
            mf.c = this;
            this.setVisible(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_jButCrearHorariosActionPerformed

    private void formWindowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosed
        // TODO add your handling code here:
        try {
            Loggin loggin = new Loggin();
            loggin.setVisible(true);
        } catch (Exception e) {

        }
    }//GEN-LAST:event_formWindowClosed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        // TODO add your handling code here:
    }//GEN-LAST:event_formWindowClosing

    private void jButVerHorariosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButVerHorariosActionPerformed
        // TODO add your handling code here:
        try {
            VisualizarHorarios vh = new VisualizarHorarios(carrera, nombre, this);
            vh.setVisible(true);
            vh.c = this;
            this.setVisible(false);
        } catch (Exception e) {

        }
    }//GEN-LAST:event_jButVerHorariosActionPerformed

    private void jButActMateriasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButActMateriasActionPerformed
        // TODO add your handling code here:
        Object[][] matdb;
        try {
            matdb = con.showAllData(carrera, "Select * from "+carrera);
        } catch (Exception e) {
            matdb = new Object[0][0];
            JOptionPane.showMessageDialog(this, "Error en actualización, asegúrese de que tiene acceso a internet. Si el problema persiste pongase en contacto con el creador del programa.");
        }
//        System.out.println(datos.length);
        if (matdb.length > 0) {
            Archivo materiasBD = new Archivo("Materias por carrera\\" + carrera + ".txt");
            materiasBD.crearEscritura();
            for (int i = 0; i < matdb.length; i++) {
                for (int j = 0; j < matdb[0].length; j++) {
//                    System.out.print(datos[i][j] + " ");
                    if (j < matdb[0].length - 1 && j != 1) {
                        materiasBD.EscribirLinea(matdb[i][j] + " ");
                    } else {
                        materiasBD.EscribirLinea(matdb[i][j] + "");
                    }

                }
                if (i < matdb.length - 1) {
                    materiasBD.NuevaLinea();
                }

//                System.out.println("");
            }
            materiasBD.CerrarEscritura();
            crearArchivos(carrera + ".txt");
            JOptionPane.showMessageDialog(this, "Horarios de materias actualizados.");

        }
    }//GEN-LAST:event_jButActMateriasActionPerformed

    private void jButActDatosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButActDatosActionPerformed
        // TODO add your handling code here:
        actualizarDatos();
    }//GEN-LAST:event_jButActDatosActionPerformed

    public void actualizarDatos() {

//        alu = con.Log(jTextUser.getText(), jTextPass.getText());
        String alu[] = con.Log(Alumno.numControl, pass);
//        alu = con.Log("16130809", "fullcounter");

        for (String alu1 : alu) {
//            System.out.println(alu1);
        }
        if (!alu[0].equals("null") && !alu[1].equals("null")) {
            String[] split = alu[1].split(" ");
            carrera = split[0];
            String numControl = alu[0];

            for (String listado1 : listado) {
                if (listado1.equals(carrera + ".txt")) {
                    crearArchivos(listado1);
                }
            }
            try {
                boolean band = false;
                carpeta = new File(carrera + "\\Usuarios");
//                System.out.println(Arrays.toString(carpeta.list()));
                listado = carpeta.list() == null ? null : carpeta.list();
                try {
//                    System.out.println(listado);
                } catch (Exception ex) {
                    listado = null;
                }

//                System.out.println(user);
                String nombreArchivo = numControl;
                nombreArchivo = nombreArchivo.toUpperCase();
                carpeta = new File(carrera + "\\Usuarios");
//                carpeta = new File(PathComplemento.substring(0, PathComplemento.length()-5)+carrera + "\\Usuarios");

//                    try {
//                        listado = carpeta.list();
//                        if (listado.length > 0) {
//                            for (int i = 0; i < listado.length; i++) {
//                                if (nombreArchivo.equals(listado[i])) {
//                                    band = true;
//                                    break;
//                                }
//                            }
//                        }
//                    } catch (Exception e) {
//
//                    }
                if (band == false) {

                    //                            System.out.println(carrera);
                    Archivo a1 = new Archivo(carrera + "\\NombreMaterias.txt");
                    
                    if (a1.crearLectura()) {

                        direct = new File(carrera + "\\Usuarios\\" + nombreArchivo);
//                                direct.mkdir();
//                                direct.createNewFile();
                        Archivo datos = new Archivo(direct + "\\datos.txt");
                        datos.crearEscritura();
                        Archivo kardex = new Archivo(direct + "\\kardex.txt");
                        kardex.crearEscritura();
                        Archivo a2 = new Archivo(direct + "\\materiasEstado.txt");
                        a2.crearEscritura();

                        ArrayList<String> lista = new ArrayList<>();
                        String line = a1.LeerLinea();
                        band = true;
                        int cont = 0;
                        while (line != null) {
                            band = true;
                            for (int j = 0; j < lista.size(); j++) {
                                if (line.equals(lista.get(j)) || line.equals("RESIDENCIA") || line.equals("RESIDENCIA PROFESIONAL") || line.equals("TUTORIA")) {
                                    band = false;
                                }
                            }
                            if (band == true) {
                                lista.add(line);
                            }
                            line = a1.LeerLinea();
                        }
                        String[][] kard = con.Kardex(numControl,carrera);
                        boolean coincide = false;
                        for (int j = 0; j < lista.size(); j++) {
                            coincide = false;
                            for (String[] kard1 : kard) {
//                                        System.out.println(lista.get(j) + ".equals" + (kard1[1]));
                                if (lista.get(j).equals(kard1[1])) {
                                    coincide = true;
//                                            System.out.println(lista.get(j) + ".equals" + (kard1[1]));
                                    break;
                                }

                            }
//                                    System.out.println("");
                            if (cont > 0 && coincide == false) {
                                a2.NuevaLinea();
                            }
                            if (coincide == false) {
                                a2.EscribirLinea(lista.get(j));
                                cont++;
                            }

                        }
                        for (int i = 0; i < kard.length; i++) {
                            kardex.EscribirLinea(kard[i][0] + " - " + kard[i][1] + " - " + kard[i][2] + " - " + kard[i][3] + " - " + kard[i][4]);
                            if (i < kard.length - 1) {
                                kardex.NuevaLinea();
                            }
                            creditos += Integer.valueOf(kard[i][4]);
                        }

                        CrearArchivoMateriasCursando();
                        for (int i = 0; i < alu.length; i++) {
                            datos.EscribirLinea(alu[i]);
                            datos.NuevaLinea();
                        }
                        datos.EscribirLinea(creditos + "");
                        a1.CerrarLectura();
                        a2.CerrarEscritura();
                        datos.CerrarEscritura();
                        kardex.CerrarEscritura();

                    } else {
                    }
                }
                refresh();
                JOptionPane.showMessageDialog(this, "Datos de alumno actualizados");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void CrearArchivoMateriasCursando() {
         
        direct = new File(carrera + "\\Usuarios\\" + Alumno.numControl);
        Archivo cursando = new Archivo(direct + "\\materiasCursando.txt");
        cursando.crearEscritura();
        ArrayList<String[]> matCurs = con.Cursando(Alumno.numControl);
        String lineaMat;
        for (int i = 0; i < matCurs.size(); i++) {
            lineaMat = "";
            for (int j = 0; j < matCurs.get(i).length; j++) {
                lineaMat += (matCurs.get(i)[j]);
                if (j < matCurs.get(i).length - 1) {
                    lineaMat += " - ";
                }

            }
            cursando.EscribirLinea(lineaMat);
            if (i < matCurs.size() - 1) {
                cursando.NuevaLinea();
            }
        }
        cursando.CerrarEscritura();
    }

    public void crearArchivos(String nombre) {
        try {
            Archivo archivo = new Archivo("Materias por carrera\\" + nombre);
            archivo.crearLectura();
            String line = archivo.LeerLinea();
            ArrayList<String> materias = new ArrayList<>();
            ArrayList<String> claves = new ArrayList<>();
            ArrayList<String> horarios = new ArrayList<>();
            ArrayList<String> maestros = new ArrayList<>();
            ArrayList<String> costos = new ArrayList<>();
            ArrayList<String> semestres = new ArrayList<>();
            while (line != null) {
                String cadena = "";
                String cadenaTotal = "";
                String lineNueva = "";
                for (int i = 0; i < line.length(); i++) {
                    if (line.charAt(i) != " ".charAt(0) && line.charAt(i) != "\t".charAt(0) && i < line.length()) {
                        if (((int) line.charAt(i)) < 47 || ((int) line.charAt(i)) > 58) {
                            cadena += line.charAt(i) + "";

                        } else {

                            break;
                        }
                    } else {
                        cadenaTotal += cadena + " ";
                        cadena = "";

                    }
                }
                materias.add(cadenaTotal);

                for (int i = cadenaTotal.length(); i < line.length(); i++) {
                    lineNueva += line.charAt(i);
                }
                line = lineNueva;
                cadena = "";
                cadenaTotal = "";
                lineNueva = "";
                for (int i = 0; i < line.length(); i++) {
                    if (line.charAt(i) != " ".charAt(0) && line.charAt(i) != "\t".charAt(0)) {
                        cadena += line.charAt(i);
                    } else {
                        break;
                    }
                }
                claves.add(cadena);
                for (int i = cadena.length(); i < line.length(); i++) {
                    lineNueva += line.charAt(i);
                }
                line = lineNueva;
                cadena = "";
                cadenaTotal = "";
                lineNueva = "";
                int con = 0;
                for (int i = 1; i < line.length(); i++) {

                    if (line.charAt(i) == " ".charAt(0) || line.charAt(i) == "\t".charAt(0)) {
                        con++;
                        if (con == 5) {
                            cadenaTotal += cadena;
                            break;
                        } else {
                            cadena += line.charAt(i);
                        }
                    } else {
                        cadena += line.charAt(i);
                    }
                }

                horarios.add(cadena);
                for (int i = cadena.length() + 2; i < line.length(); i++) {
                    lineNueva += line.charAt(i);
                }
                line = lineNueva;

                maestros.add(line.substring(0, line.length() - 4));
                costos.add(line.substring(line.length() - 3, line.length() - 2));
                semestres.add(line.substring(line.length() - 1, line.length()));
//                System.out.println("costos "+line.substring(line.length()-3, line.length()-2)+" semestres "+line.substring(line.length()-1, line.length()));
                line = archivo.LeerLinea();

            }
            String n = "";
            for (int i = 0; i < nombre.length() - 4; i++) {
                n += nombre.charAt(i) + "";
            }
            String materia = "";
            for (int i = 0; i < materias.size(); i++) {
                for (int j = 0; j < materias.get(i).length() - 1; j++) {
                    materia += materias.get(i).charAt(j);
                }
                materias.set(i, materia);
                materia = "";
            }
            nombre = n;
            Archivo NombreMaterias = new Archivo(n + "\\NombreMaterias.txt");
            Archivo ClaveMaterias = new Archivo(n + "\\ClaveMaterias.txt");
            Archivo HorarioMaterias = new Archivo(n + "\\HorarioMaterias.txt");
            Archivo MaestroMaterias = new Archivo(n + "\\MaestroMaterias.txt");
            Archivo CostosMaterias = new Archivo(n + "\\Costos.txt");
            Archivo SemestresMaterias = new Archivo(n + "\\Semestres.txt");
            NombreMaterias.crearEscritura();
            ClaveMaterias.crearEscritura();
            HorarioMaterias.crearEscritura();
            MaestroMaterias.crearEscritura();
            CostosMaterias.crearEscritura();
            SemestresMaterias.crearEscritura();
            for (int i = 0; i < materias.size(); i++) {
                NombreMaterias.EscribirLinea(materias.get(i));
                ClaveMaterias.EscribirLinea(claves.get(i));
                HorarioMaterias.EscribirLinea(horarios.get(i));
                MaestroMaterias.EscribirLinea(maestros.get(i));
                CostosMaterias.EscribirLinea(costos.get(i));
                SemestresMaterias.EscribirLinea(semestres.get(i));
                if (i < materias.size() - 1) {
                    NombreMaterias.NuevaLinea();
                    ClaveMaterias.NuevaLinea();
                    HorarioMaterias.NuevaLinea();
                    MaestroMaterias.NuevaLinea();
                    CostosMaterias.NuevaLinea();
                    SemestresMaterias.NuevaLinea();

                }
            }

            NombreMaterias.CerrarEscritura();
            ClaveMaterias.CerrarEscritura();
            HorarioMaterias.CerrarEscritura();
            MaestroMaterias.CerrarEscritura();
            CostosMaterias.CerrarEscritura();
            SemestresMaterias.CerrarEscritura();
        } catch (Exception e) {

        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;

                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Cuenta.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Cuenta.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Cuenta.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Cuenta.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Cuenta("", "", "", "").setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButActDatos;
    private javax.swing.JButton jButActMaterias;
    private javax.swing.JButton jButCrearHorarios;
    private javax.swing.JButton jButVerHorarios;
    private javax.swing.JLabel jLabAct;
    private javax.swing.JLabel jLabCarrera;
    private javax.swing.JLabel jLabCredit;
    private javax.swing.JLabel jLabCuenta;
    private javax.swing.JLabel jLabImagen;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTable jTableKardex;
    private javax.swing.JTable jTableMateriasCursando;
    // End of variables declaration//GEN-END:variables

}
