
SET @codigo_cliente = 127;


-- First remove all concepts for all presupuestos from "concepto_presupuesto" table
DELETE FROM concepto_presupuesto
WHERE Nro_Presupuesto IN (
	SELECT Nro_Presupuesto
	FROM presupuesto
	WHERE Codigo_Cliente = @codigo_cliente
);

-- Second remove all budgets from the client from "presupuesto"
DELETE FROM presupuesto
WHERE Codigo_Cliente = @codigo_cliente;


-- Third remove all transactions from the client from "transaccion"
DELETE FROM transaccion
WHERE Codigo_Cliente = @codigo_cliente;

-- Fourth remove client account from the client "Cuenta_corriente"
DELETE FROM cuenta_corriente
WHERE Codigo_Cliente = @codigo_cliente;

-- Fifth and last, remove client from "Cliente" table
DELETE FROM Cliente
WHERE Codigo_Cliente = @codigo_cliente;


