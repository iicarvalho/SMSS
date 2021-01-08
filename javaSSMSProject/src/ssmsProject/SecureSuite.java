/*
 * Integrantes:
 * Igor Inácio de Carvalho Silva - 725804
 * Vitoria Rodrigues Silva - 726598
 * */
package ssmsProject;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import java.security.SecureRandom;
import java.util.HashMap;

/**
 *
 * @author yeda
 */
public class SecureSuite {
    private int alg;
    private int mode;
    private int pad;
    public static final HashMap<Integer,String> algMap = new HashMap<Integer,String>();
    {
        algMap.put(0,"AES"); // 128 bits
        algMap.put(1,"AES"); // 192 bits
        algMap.put(2,"AES"); // 256 bits
        algMap.put(3,"DES"); // 64 bits
        algMap.put(4,"3DES-EDE2"); // 64 bits
        algMap.put(5,"3DES-EDE3"); // 64 bits
    };

    public static final HashMap<Integer,String> modeMap = new HashMap<Integer,String>();
    {
        modeMap.put(0,"ECB");
        modeMap.put(1,"CBC");
        modeMap.put(2,"CFB1");
        modeMap.put(3,"CFB8");
        modeMap.put(4,"CFB64");
        modeMap.put(5,"CFB128");
        modeMap.put(6,"CTR");
    };


    public static final HashMap<Integer,String> padMap = new HashMap<Integer,String>();
    {
        padMap.put(0,"NoPadding");
        padMap.put(1,"PKCS5Padding");
    };


    SecureSuite(int algoritmo, int modo, int padding){
        this.alg = algoritmo;
        this.mode = modo;
        this.pad = padding;
    }

    public int getAlg() {
        return alg;
    }

    public void setAlg(int alg) {
        this.alg = alg;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public void setPad(int pad) {
        this.pad = pad;
    }

    public int getMode() {
        return mode;
    }

    public int getPad() {
        return pad;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final SecureSuite other = (SecureSuite) obj;
        if (this.alg != other.alg) {
            return false;
        }
        if (this.mode != other.mode) {
            return false;
        }
        if (this.pad != other.pad) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        return hash;
    }

    @Override
    public String toString(){
        String ret;

        ret = algMap.get(new Integer(this.alg)) + "/" +
                modeMap.get(new Integer(this.mode)) + "/" +
                padMap.get(new Integer(this.pad));
        return ret;
    }

    public String getTextAlg(){
        return algMap.get(this.alg);
    }
}

