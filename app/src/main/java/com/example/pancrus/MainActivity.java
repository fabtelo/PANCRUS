package com.example.pancrus;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
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
    private String uNodo;
    
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
    }

    private void seteaFecha() {
        anoET.setText(STagno);
        mesET.setText(STmes);
        diaET.setText(STdia);
    }

    private void recuNodo() {
        nodo.child("Unodo").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                uNodo=snapshot.child("dia").getValue().toString()+"-"+snapshot.child("mes").getValue().toString()+
                        "-"+snapshot.child("agno").getValue().toString();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(),"error recuNodo",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void regDeposito() {
        if(depositoET.getText().toString().length()>1){
            nodo.child("depositos").child(STagno).child(STmes).child(STdia).setValue(depositoET.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    esconderKeyboard();
                    depositoET.setText(null);
                }
            });
        }else Toast.makeText(this,"FALTAN DATOS",Toast.LENGTH_SHORT).show();
    }

    private void regCaja() {
        if(cajaET.getText().toString().length()>1&&anoET.getText().toString().length()>2&&mesET.getText().toString().length()==1&&diaET.getText().toString().length()==1){
            nodo.child("Ucierre").setValue(cajaET.getText().toString());
            nodo.child("nodos").child(uNodo).child("cierres").child(STmes).child(STdia).setValue(cajaET.getText().toString());
            nodo.child("nodos").child(anoET.getText().toString()).child(mesET.getText().toString()).child(diaET.getText().toString()).setValue(cajaET.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    esconderKeyboard();
                    cajaET.setText(null);
                    anoET.setText(null);
                    mesET.setText(null);
                    diaET.setText(null);
                }
            });
        }else Toast.makeText(this,"FALTAN DATOS",Toast.LENGTH_SHORT).show();
    }

    private void regVenta() {
        if(ventaET.getText().toString().length()>0&&anoET.getText().toString().length()>2&&mesET.getText().toString().length()==1&&diaET.getText().toString().length()==1){
            nodo.child("ventas").child(anoET.getText().toString()).child(mesET.getText().toString()).child(diaET.getText().toString()).
                    child(spVenta.getSelectedItem().toString()).setValue(ventaET.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            esconderKeyboard();
                            ventaET.setText(null);
                            anoET.setText(null);
                            mesET.setText(null);
                            diaET.setText(null);
                        }
                    });
        }else Toast.makeText(this,"FALTAN DATOS",Toast.LENGTH_SHORT).show();
    }

    private void regGasto() {
        if(gastoET.getText().toString().length()>0){
            if(spGasto.getSelectedItem().toString().equalsIgnoreCase("carne")){
                nodo.child("Unodo").child("agno").setValue(STagno);
                nodo.child("Unodo").child("mes").setValue(STmes);
                nodo.child("Unodo").child("dia").setValue(STdia).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        recuNodo();
                    }
                });
                String jecha=STdia+"-"+STmes+"-"+STagno;
                nodo.child("nodos").child(jecha).child("carne").setValue(gastoET.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        gastoET.setText(null);
                        esconderKeyboard();
                    }
                });
            }else {
                nodo.child("nodos").child(uNodo).child(spGasto.getSelectedItem().toString()).setValue(gastoET.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        gastoET.setText(null);
                        esconderKeyboard();
                    }
                });
            }

        }else Toast.makeText(this,"INGRESE EL MONTO DEL GASTO",Toast.LENGTH_SHORT).show();
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
}