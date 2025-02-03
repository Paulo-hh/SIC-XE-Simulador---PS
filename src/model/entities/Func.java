package model.entities;

public class Func {
    
    public static String encontrarValor(int valor, String hexaValor, int tamanho) {
        String novoHexa = hexaValor;
        if (valor >= 0) {
            novoHexa = Func.tamString(novoHexa, tamanho * 2);
        } else {
            novoHexa = Func.tamSpace(novoHexa, tamanho * 2);
        }
        return novoHexa;
    }

    public static Integer hexa_para_Int(String hexa) {
        try {
            int bits = hexa.length() * 4;
            int valor = Integer.parseInt(hexa, 16);
            if ((valor & (1 << (bits - 1))) != 0) {
                valor -= (1 << bits);
            }
            return valor;
        } catch (Exception e) {
            return null;
        }
    }

    public static String int_para_Hexa(int numero, int bits) {
        try {
            if (numero < 0) {
                return Integer.toHexString((1 << bits) + numero).toUpperCase();
            } else {
                return Integer.toHexString(numero).toUpperCase();
            }
        } catch (Exception e) {
            return null;
        }
    }

    public static String asciiParaHex(String valor) {
    	return Integer.toHexString(Integer.parseInt(valor)).toUpperCase();
    }

    public static String somarHexa(String x, String y) {
        if (x.length() != y.length()) {
            System.out.println("hexas ilegais");
        }
        int tamanho = x.length() * 4;
        int resultado = hexa_para_Int(x) + hexa_para_Int(y);
        return int_para_Hexa(resultado, tamanho);
    }

    public static String subtrairHexa(String x, String y){
        if (x.length() != y.length()) {
            System.out.println("hexas ilegais");
        }
        int tamanho = x.length() * 4;
        int resultado = hexa_para_Int(x) - hexa_para_Int(y);
        return int_para_Hexa(resultado, tamanho);
    }

    public static int compareHex(String x, String y) {
        int intX = hexa_para_Int(x);
        int intY = hexa_para_Int(y);
        if (intX == intY) {
            return 2;
        } else if (intX > intY) {
            return 1;
        } else {
            return 0;
        }
    }

    public static String[] dividirByte(String hexa) {
        int tamanho = hexa.length();
        String[] listaBytes = new String[tamanho / 2];
        for (int i = 0; i < tamanho; i += 2) {
            listaBytes[i / 2] = hexa.substring(i, i + 2);
        }
        return listaBytes;
    }
    
    public static String tamString(String endereco, int tam) {
    	String resultado = endereco;
    	if(endereco == null) {
    		return null;
    	}
    	if(endereco.length() < tam) {
    		for(int i=endereco.length(); i<tam; i++) {
    			String novo = "0";
    			resultado = novo + resultado;
    		}
    		return resultado;
    	}
    	else {
    		if(endereco.length() == tam) {
    			return endereco;
    		}
    		else {
    			return endereco.substring(endereco.length()-4, endereco.length());
    		}
    	}
    }
    
    public static String tamSpace(String endereco, int tam) {
    	String resultado = endereco;
    	if(endereco.length() < tam) {
    		for(int i=endereco.length(); i<tam; i++) {
    			String novo = " ";
    			resultado = novo + resultado;
    		}
    		return resultado;
    	}
    	else {
    		if(endereco.length() == tam) {
    			return endereco;
    		}
    		else {
    			return endereco.substring(endereco.length()-4, endereco.length());
    		}
    	}
    }
}