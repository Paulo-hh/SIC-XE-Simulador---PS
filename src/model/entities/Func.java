package model.entities;

import java.util.ArrayList;
import java.util.List;

public class Func {
    private static final int comprimentoEndereco = 4;
    private static List<Instrucao> instrucoes;
	
    public static void setInstrucoes(List<Instrucao> instrucoes) {
		Func.instrucoes = instrucoes;
	}

	public static String encontrarValor(int valor, String hexaValor, int tamanho) {
        String novoHexa = hexaValor;
        if (valor >= 0) {
            novoHexa = Func.preencherZeros(novoHexa, 6);
        } else {
            novoHexa = Func.preencherF(novoHexa, 6);
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
            System.out.println("Erro na conversão de hexa para inteiro");
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

    public static String somarHexa(String x, String y) {
        if (x.length() != y.length()) {
            System.out.println("hexas ilegais");
        }
        int tamanho = x.length() * 4;
        int resultado = hexa_para_Int(x) + hexa_para_Int(y);
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
    
    public static int obterIndice(String rotulo) {
		for (int i = 0; i < instrucoes.size(); i++) {
			if (instrucoes.get(i).getRotulo().equals(rotulo)) {
				return i;
			}
		}
		return -1;
	}
    
    public static String obterDado(String enderecoInicial, int tamanho, Memoria conjuntoMemoria) {
		String endereco = enderecoInicial;
		String memoriaStringHexa = "";
		for (int i = 0; i < tamanho; i++) {
			memoriaStringHexa = memoriaStringHexa.concat(conjuntoMemoria.getMemoria(endereco));
			endereco = Func.somarHexa(endereco, Func.preencherZeros("1", comprimentoEndereco)).toUpperCase();
			endereco = Func.preencherZeros(endereco, 4);
		}
		return memoriaStringHexa;
	}

    public static List<String> dividirBytes(String hexaString) {
		List<String> byteLista = new ArrayList<>();
		for (int i = 0; i < hexaString.length(); i += 2) {
			byteLista.add(hexaString.substring(i, Math.min(i + 2, hexaString.length())));
		}
		return byteLista;
	}
    
    public static String preencherZeros(String endereco, int tam) { 
    	String resultado = endereco;
    	if(endereco == null) {
    		return "000000";
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
    
    public static String preencherF(String endereco, int tam) {
    	String resultado = endereco;
    	if(endereco.length() < tam) {
    		for(int i=endereco.length(); i<tam; i++) {
    			String novo = "F";
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
    
    public static Instrucao obterInstrucao(String rotulo) throws Exception {
		for (Instrucao instr : instrucoes) {
			if (instr.getRotulo().equals(rotulo)) {
				return instr;
			}
		}
		throw new Exception("ERRO: o rótulo - '" + rotulo + "' pode estar errado");
	}
    
}