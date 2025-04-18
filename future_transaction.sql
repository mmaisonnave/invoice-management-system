
CREATE TABLE IF NOT EXISTS transacciones_futuras (
    Nro_transaccion_futura INT NOT NULL AUTO_INCREMENT,
    Codigo_Cliente INT(11) NOT NULL,
    Nro_Presupuesto int(11) NOT NULL,
    Fecha DATE NOT NULL,
    Evento char(1) NOT NULL,
    Monto DOUBLE NOT NULL,
    Concepto varchar(100) DEFAULT NULL,
    PRIMARY KEY (Nro_transaccion_futura),
    FOREIGN KEY (Nro_Presupuesto) REFERENCES  presupuesto (Nro_Presupuesto),
    FOREIGN KEY (Codigo_Cliente) REFERENCES  cliente (Codigo_Cliente)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;


SELECT * FROM Cuenta_corriente WHERE Codigo_Cliente in (127,128);

INSERT IGNORE INTO transacciones_futuras (Nro_transaccion_futura, Codigo_Cliente, Nro_Presupuesto, Fecha, Evento, Monto, Concepto)
VALUES
    (1, 127, 13530, '2024-12-19', 'P', 100, "some invoice, 100"),
    (2, 127, 13531, '2024-12-18', 'P', 360, "some invoice, 360"),
    (3, 128, 13532, '2024-12-17', 'P', 400, "some invoice Maisonnave2, 300")
    ;

SELECT * FROM transacciones_futuras;

--(
--    SELECT cc.Monto - tf.monto
--    FROM cuenta_corriente as cc
--    WHERE cc.Codigo_Cliente=tf.Codigo_Cliente) as Estado_cuenta_corriente)
--
--INSERT INTO transaccion (Codigo_Cliente, Nro_Presupuesto, Fecha, Evento, Monto, Concepto, Estado_cuenta_corriente )
--VALUES


-- Records to make effective in transactions:

--INSERT INTO transaccion (Codigo_Cliente, Nro_Presupuesto, Fecha, Evento, Monto, Concepto, Estado_cuenta_corriente )
--VALUES
WITH para_efectivizar AS (
  SELECT Codigo_Cliente, Fecha, Evento, Monto, Concepto
  FROM transacciones_futuras AS tf
  WHERE Fecha <= CURRENT_DATE
),
max_val AS (
  SELECT MAX(Nro_transaccion) AS max_transaction_number
  FROM transaccion
)
SELECT
  max_val.max_transaction_number + ROW_NUMBER() OVER (ORDER BY (SELECT NULL)) AS transaction_number,
  pe.*
FROM para_efectivizar AS pe
CROSS JOIN max_val;
--SELECT cc.Codigo_cliente, pe.Fecha, pe.Evento, pe. Monto, pe.Concepto, (cc.Monto - pe.Monto) AS estado_cuenta_corriente
--FROM cuenta_corriente AS cc
--JOIN para_efectivizar AS pe
--ON cc.Codigo_Cliente = pe.Codigo_Cliente
--ORDER BY Fecha ASC;



--- I need to add the transaction number to the transacciones_futuras, use that number to create the transactions.
--- Use the future transaction list to update the invoice table


DROP TABLE transacciones_futuras;

