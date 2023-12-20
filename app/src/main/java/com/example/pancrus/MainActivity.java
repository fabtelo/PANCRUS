package com.example.pancrus;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    private Button btnGasto,btnVenta,btnCaja,btnDeposito;
    private EditText gastoET,anoET,mesET,diaET,ventaET,cajaET,depositoET;
    private Spinner spGasto,spVenta;
    private TextView txtGastos,txtBalance,txtDisplay;
    private String uNodo,uCierre,pCierre;
    private String hamburguesa,lomito,refresco;
    private String uNodoAnio,uNodoMes,uNodoDia;

    //herramientas spiner
    private ArrayList<String> arrayItemGasto= new ArrayList<String>();
    private ArrayAdapter<String> adapterArrayGasto;
    private ArrayList<String> arrayItemVenta=new ArrayList<>();
    private ArrayAdapter<String> adapterArrayVenta;
//coneccion con firebase database
    private DatabaseReference nodo= FirebaseDatabase.getInstance().getReference();
//seteo de FECHA
    private Date fecha=new Date();
    private SimpleDateFormat agno=new SimpleDateFormat("YYYY");
    private SimpleDateFormat mes=new SimpleDateFormat("MM");
    private SimpleDateFormat dia=new SimpleDateFormat("dd");
    private String STagno=agno.format(fecha);
    private String STmes=mes.format(fecha);
    private String STdia=dia.format(fecha);

//declaracion variables para informe
    private int ucierreInt,pcierreInt,burguerInt,refreInt,lomitoInt;
    private ArrayList<Integer> arrayGastosInt,arrayDepositoInt;
    private ArrayList<String> arrayGastosString,arrayDepositoString;
    private int totVentasInt,totGastosInt;
//metodo OnCreate
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recuNodo();
        adapterArrayGasto=new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,arrayItemGasto);
        adapterArrayVenta=new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,arrayItemVenta);
//instanciando views
        btnGasto=findViewById(R.id.button);
        btnVenta=findViewById(R.id.button2);
        btnCaja=findViewById(R.id.button3);
        btnDeposito=findViewById(R.id.button4);

        gastoET=findViewById(R.id.editTextText);
        anoET=findViewById(R.id.agnoET);
        mesET=findViewById(R.id.mezET);
        diaET=findViewById(R.id.diaET);
        ventaET=findViewById(R.id.editTextText4);
        cajaET=findViewById(R.id.editTextText5);
        depositoET=findViewById(R.id.editText6);
        seteaFecha();

        spGasto=findViewById(R.id.spinner);
        spGasto.setAdapter(adapterArrayGasto);
        spVenta=findViewById(R.id.spinner3);
        spVenta.setAdapter(adapterArrayVenta);

        txtBalance=findViewById(R.id.textviewBalance);
        txtGastos=findViewById(R.id.textViewGastos);
        txtDisplay=findViewById(R.id.textView);
//agregando onclick accion en spinners
        seteaItemVenta();
        seteaItemGastos();
//agregando onclick a botones
        btnGasto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                regGasto();
            }
        });
        btnVenta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                regVenta();
            }
        });
        btnCaja.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                regCaja();
            }
        });
        btnDeposito.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                regDeposito();
            }
        });
//arraylist para reporte
        arrayGastosInt=new ArrayList<>();
        arrayGastosString=new ArrayList<>();
        arrayDepositoInt=new ArrayList<>();
        arrayDepositoString=new ArrayList<>();
        txtBalance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                irInforme();
            }
        });
    }

    private void seteaFecha() {
        anoET.setText(STagno);
        mesET.setText(STmes);
        diaET.setText(STdia);
    }

    private void recuNodo() {
        nodo.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                uNodo=snapshot.child("Unodo").child("dia").getValue().toString()+
                        "-"+snapshot.child("Unodo").child("mes").getValue().toString()+"-"+snapshot.child("Unodo").child("agno").getValue().toString();
                uNodoAnio=snapshot.child("Unodo").child("agno").getValue().toString();
                uNodoMes=snapshot.child("Unodo").child("mes").getValue().toString();
                uNodoDia=snapshot.child("Unodo").child("dia").getValue().toString();
                uCierre=snapshot.child("Ucierre").getValue().toString();
                pCierre=snapshot.child("Pcierre").getValue().toString();
                seteaInforme();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(),"error recuNodo",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void regDeposito() {
        if(depositoET.getText().toString().length()>1){
            nodo.child("nodos").child(uNodo).child("depositos").child(STdia).setValue(depositoET.getText().toString());
            nodo.child("depositos").child(STagno).child(STmes).child(STdia).setValue(depositoET.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    esconderKeyboard();
                    depositoET.setText(null);
                    seteaInforme();
                }
            });
        }else Toast.makeText(this,"FALTAN DATOS",Toast.LENGTH_SHORT).show();
    }

    private void regCaja() {
        if(cajaET.getText().toString().length()>1&&anoET.getText().toString().length()>1&&mesET.getText().toString().length()>0&&diaET.getText().toString().length()>0){
            nodo.child("Ucierre").setValue(cajaET.getText().toString());
            nodo.child("caja").child(STagno).child(STmes).child(STdia).setValue(cajaET.getText().toString());
            nodo.child("nodos").child(uNodo).child("Ucierre").setValue(cajaET.getText().toString());
            nodo.child("nodos").child(uNodo).child("cierres").child(STdia).setValue(cajaET.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    esconderKeyboard();
                    cajaET.setText(null);
                    seteaInforme();
                }
            });
        }else Toast.makeText(this,"FALTAN DATOS",Toast.LENGTH_SHORT).show();
    }

    private void regVenta() {
        if(ventaET.getText().toString().length()>0&&anoET.getText().toString().length()>2&&mesET.getText().toString().length()>0&&diaET.getText().toString().length()>0){
            nodo.child("nodos").child(uNodo).child("ventas").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    hamburguesa=snapshot.child("hamburguesa").getValue().toString();
                    lomito=snapshot.child("lomito").getValue().toString();
                    refresco=snapshot.child("refresco").getValue().toString();
                    regVenta2();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }else Toast.makeText(this,"FALTAN DATOS",Toast.LENGTH_SHORT).show();
    }

    private void regVenta2() {
        DatabaseReference niodo=FirebaseDatabase.getInstance().getReference("nodos").child(uNodo).child("ventas");
        int ventaInt=Integer.parseInt(ventaET.getText().toString());
        switch (spVenta.getSelectedItem().toString()){
            case "hamburguesa": niodo.child("hamburguesa").setValue(ventaInt+Integer.parseInt(hamburguesa)).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    ventaET.setText(null);
                    esconderKeyboard();
                    seteaInforme();
                }
            });
                break;
            case "lomito": niodo.child("lomito").setValue(ventaInt+Integer.parseInt(lomito)).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    ventaET.setText(null);
                    esconderKeyboard();
                    seteaInforme();
                }
            });
                break;
            case "refresco": niodo.child("refresco").setValue(ventaInt+Integer.parseInt(refresco)).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    ventaET.setText(null);
                    esconderKeyboard();
                }
            });
                break;
        }
    }

    private void regGasto() {
        if(gastoET.getText().toString().length()>0){
            if(spGasto.getSelectedItem().toString().equalsIgnoreCase("carne")){
               if (cajaET.getText().length()>1){
                   nodo.child("Pcierre").setValue(cajaET.getText().toString());
                   nodo.child("nodox").child(uNodoAnio).child(uNodoMes).child(uNodoDia).child("gastos").child("carne").setValue(gastoET.getText().toString());
                   nodo.child("nodos").child(uNodo).child("gastos").child("carne").setValue(gastoET.getText().toString()).
                           addOnSuccessListener(new OnSuccessListener<Void>() {
                       @Override
                       public void onSuccess(Void unused) {
                           String unodo=STdia+"-"+STmes+"-"+STagno;

                           nodo.child("Unodo").child("agno").setValue(STagno);
                           nodo.child("Unodo").child("mes").setValue(STmes);
                           nodo.child("Unodo").child("dia").setValue(STdia).addOnSuccessListener(new OnSuccessListener<Void>() {
                               @Override
                               public void onSuccess(Void unused) {
                                   recuNodo();
                                   nodo.child("nodox").child(uNodoAnio).child(uNodoMes).child(uNodoDia).child("Ucierre").setValue(0);
                                   nodo.child("nodox").child(uNodoAnio).child(uNodoMes).child(uNodoDia).child("Pcierre").setValue(cajaET.getText().toString());
                                   nodo.child("nodos").child(unodo).child("Ucierre").setValue(0);
                                   nodo.child("nodos").child(unodo).child("Pcierre").setValue(cajaET.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                       @Override
                                       public void onSuccess(Void unused) {
                                           cajaET.setText(null);
                                           seteaNodo();
                                       }
                                   });
                                   gastoET.setText(null);
                                   esconderKeyboard();
                                   seteaInforme();
                               }
                           });
                       }
                   });
               }else Toast.makeText(getApplicationContext(),"haz tu cierre de caja",Toast.LENGTH_SHORT).show();
            }else {
                nodo.child("nodos").child(uNodo).child("gastos").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.child(spGasto.getSelectedItem().toString()).exists()){
                            int e=Integer.parseInt(snapshot.child(spGasto.getSelectedItem().toString()).getValue().toString());
                            e=e+Integer.parseInt(gastoET.getText().toString());
                            nodo.child("nodox").child(uNodoAnio).child(uNodoMes).child(uNodoDia).child("gastos").child(spGasto.getSelectedItem().toString()).setValue(e);
                            nodo.child("nodos").child(uNodo).child("gastos").child(spGasto.getSelectedItem().toString()).setValue(e).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    gastoET.setText(null);
                                    esconderKeyboard();
                                    seteaInforme();
                                }
                            });
                        }else{
                            nodo.child("nodox").child(uNodoAnio).child(uNodoMes).child(uNodoDia).child("gastos").child(spGasto.getSelectedItem().toString()).setValue(gastoET.getText().toString());
                            nodo.child("nodos").child(uNodo).child("gastos").child(spGasto.getSelectedItem().toString()).setValue(gastoET.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    gastoET.setText(null);
                                    esconderKeyboard();
                                    seteaInforme();
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

        }else Toast.makeText(this,"INGRESE EL MONTO DEL GASTO",Toast.LENGTH_SHORT).show();
    }

    private void seteaNodo() {
        nodo.child("nodos").child(uNodo).child("ventas").child("hamburguesa").setValue(0);
        nodo.child("nodos").child(uNodo).child("ventas").child("refresco").setValue(0);
        nodo.child("nodos").child(uNodo).child("ventas").child("lomito").setValue(0);
    }

    private void seteaItemVenta() {
        nodo.child("item2").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot item:snapshot.getChildren()){
                    arrayItemVenta.add(item.getKey().toString());
                }adapterArrayVenta.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
    private void seteaItemGastos() {
        //Toast.makeText(this,"gastos",Toast.LENGTH_SHORT).show();
        nodo.child("items").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot item:snapshot.getChildren()){
                    arrayItemGasto.add(item.getKey().toString());
                }adapterArrayGasto.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void esconderKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
    private void seteaInforme() {
        arrayGastosString.clear();
        arrayGastosInt.clear();
        arrayDepositoString.clear();
        arrayDepositoInt.clear();
        nodo.child("nodos").child(uNodo).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ucierreInt=Integer.parseInt(snapshot.child("Ucierre").getValue().toString());
                pcierreInt=Integer.parseInt(snapshot.child("Pcierre").getValue().toString());
                burguerInt=Integer.parseInt(snapshot.child("ventas").child("hamburguesa").getValue().toString());
                lomitoInt=Integer.parseInt(snapshot.child("ventas").child("lomito").getValue().toString());
                refreInt=Integer.parseInt(snapshot.child("ventas").child("refresco").getValue().toString());
                if(snapshot.child("gastos").exists()){
                    for (DataSnapshot item:snapshot.child("gastos").getChildren()){
                        arrayGastosString.add(item.getKey().toString());
                        arrayGastosInt.add(Integer.parseInt(item.getValue().toString()));
                    }
                }
                if (snapshot.child("depositos").exists()){
                    for (DataSnapshot iten:snapshot.child("depositos").getChildren()){
                        arrayDepositoString.add(iten.getKey().toString());
                        arrayDepositoInt.add(Integer.parseInt(iten.getValue().toString()));
                    }
                }
                seteaInforme2();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void seteaInforme2() {
        totVentasInt=burguerInt*10+lomitoInt*15+refreInt*2;
        totGastosInt=0;
        if(arrayGastosInt.size()>0){
            for (Integer item:arrayGastosInt){
                totGastosInt=totGastosInt+item;
            }
        }
        int gananciaInt=totVentasInt-totGastosInt;
        nodo.child("nodos").child(uNodo).child("ganancia").setValue(gananciaInt);
        String gastosST="";
        if (arrayGastosInt.size()>0){
            for (int i=0;i<arrayGastosInt.size();i++){
                gastosST=gastosST+arrayGastosString.get(i).toString()+"="+Integer.toString(arrayGastosInt.get(i))+"\n";
            }
        }
        txtDisplay.setText("Inicio de caja="+pCierre+"\n"+gastosST+"Total Gastos="+totGastosInt+"\n"+"Total Ventas="+totVentasInt+"\n"+"Ganancia= "+gananciaInt);
        Toast.makeText(this,uNodo,Toast.LENGTH_SHORT).show();
    }
    private void irInforme() {
        Intent intent=new Intent(this, Informes.class);
        startActivity(intent);
    }
}