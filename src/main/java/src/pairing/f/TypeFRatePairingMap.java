package src.pairing.f;

import src.api.Element;
import src.api.Pairing;
import src.api.Point;
import src.api.Polynomial;
import src.field.gt.GTFiniteElement;
import src.field.gt.GTFiniteField;
import src.field.poly.PolyModField;
import src.pairing.f.map.AbstractPairingMap;

import java.math.BigInteger;

/**
 * Created by mzy on 2017/5/15.
 */
public class TypeFRatePairingMap extends AbstractPairingMap{

    private TypeFPairing pairingdata;

    public TypeFRatePairingMap(TypeFPairing pairing){
        super(pairing);
        this.pairingdata=pairing;
    }

    public Element pairing(Point P, Point Q) {

        BigInteger a=pairingdata.x.multiply(BigInteger.valueOf(6)).add(BigInteger.valueOf(2));
        Point t=(Point) Q.duplicate();
        Polynomial f=(Polynomial) pairingdata.Fq12.newOneElement();

          for (int i=a.bitLength()-2;i>=0;i--){

            f.square().mul(lineqq(t,P,pairingdata.Fq12));
            t.twice();
            if(a.testBit(i)){

                Element t0=linetq(t,Q,P,pairingdata.Fq12);
                Element t00=linetq2(t,Q,P,pairingdata.Fq12);
                Element t1=linerq(t,Q,P,pairingdata.Fq12);
                boolean b=t1.equals(t00);

                f.mul(linetq(t,Q,P,pairingdata.Fq12));
                t.add(Q);
            }
        }
        Point Q1=fob(Q,pairingdata.q);
        Point Q2=fob2(Q,pairingdata.q);
        f.mul(linetq(t,Q1,P,pairingdata.Fq12));
        t.add(Q1);
        f.mul(linetq(t,(Point) Q2.negate(),P,pairingdata.Fq12));
        t.sub(Q2);


        return new GTFiniteElement(this,(GTFiniteField) pairingdata.getGT(),tateExp(f));




    }



    public void finalPow(Element element) {
        element.set(tateExp((Polynomial) element));
    }


    public Element tateExp(Polynomial element) {

        Polynomial x = pairingdata.Fq12.newElement();
        Polynomial y = pairingdata.Fq12.newElement();

        qPower(element, y, pairingdata.xPowq8);
        qPower(element, x, pairingdata.xPowq6);
        y.mul(x);
        qPower(element, x, pairingdata.xPowq2);

        return y.mul(x.mul(element).invert()).pow(pairingdata.tateExp);
    }

    private void qPower(Polynomial element, Polynomial e1, Element e) {
        e1.getCoefficient(0).set(element.getCoefficient(0));
        e1.getCoefficient(1).set(element.getCoefficient(1).duplicate().mul(e));

        Element epow = e.duplicate().square();
        e1.getCoefficient(2).set(element.getCoefficient(2).duplicate().mul(epow));
        e1.getCoefficient(3).set(element.getCoefficient(3).duplicate().mul(epow.mul(e)));
        e1.getCoefficient(4).set(element.getCoefficient(4).duplicate().mul(epow.mul(e)));
        e1.getCoefficient(5).set(element.getCoefficient(5).duplicate().mul(epow.mul(e)));
    }

    /**
     * t is point in Fp2
     * p is point in Fp
     */

    public Element linett(Point t, Point p, PolyModField Fp12){
        Point tx= (Point) t.getX().duplicate();
        Point ty=(Point)t.getY().duplicate();
        Element temp0=ty.duplicate().twice().twice().mul(p.getY().toBigInteger());
        Element temp1=tx.duplicate().pow(BigInteger.valueOf(2)).mul(p.getX().toBigInteger()).mul(6).negate();
        Element temp2=tx.duplicate().pow(BigInteger.valueOf(2)).mul(tx).mul(6).sub(ty.duplicate().pow(BigInteger.valueOf(2)).mul(4));
        Polynomial result=(Polynomial) Fp12.newRandomElement();
        result.getCoefficient(0).set(temp0);
        result.getCoefficient(1).set(temp1);
        result.getCoefficient(2).set(0);
        result.getCoefficient(3).set(temp2);
        result.getCoefficient(4).set(0);
        result.getCoefficient(5).set(0);

        return result;


    }

    public Element lineqq(Point q,Point p,PolyModField Fp12){
        Element Fp2One=q.getX().getField().newOneElement();

        Element temp0=q.getX().duplicate().square();
        Element temp1=q.getY().duplicate().square();
        Element temp2=temp1.duplicate().square();
        Element temp3=temp1.duplicate().add(q.getX()).square().sub(temp0).sub(temp2);
        temp3.twice();
        Element temp4=temp0.duplicate().mul(3);
        Element temp6=q.getX().duplicate().add(temp4);
        Element temp5=temp4.duplicate().square();

        Element xt=temp5.duplicate().sub(temp3.duplicate().twice().negate());
        Element zt=q.getY().duplicate().add(Fp2One).square().sub(temp1).sub(Fp2One.duplicate().square());
        Element yt=temp3.duplicate().sub(xt).mul(temp4).sub(temp2.duplicate().mul(8));
        temp3=temp4.mul(Fp2One.duplicate().square()).twice().negate();
        temp3.mul(p.getX().toBigInteger());// ???
        temp6=temp6.duplicate().square().sub(temp0).sub(temp5).sub(temp1.mul(4));
        temp0=zt.duplicate().mul(Fp2One.duplicate().square()).twice();
        temp0.mul(p.getY().toBigInteger());//??

        Polynomial l=(Polynomial) Fp12.newRandomElement();
        l.getCoefficient(0).set(temp0);
        l.getCoefficient(1).set(temp3);
        l.getCoefficient(2).set(0);
        l.getCoefficient(3).set(temp6);
        l.getCoefficient(4).set(0);
        l.getCoefficient(5).set(0);
        return l;

    }
    /**
     * t is point in Fp2
     * q is point in Fp2
     * p is point in Fp
     */
    public Element linetq(Point t,Point q,Point p,PolyModField Fp12){

        Point tx=(Point) t.getX().duplicate();
        Point ty=(Point) t.getY().duplicate();
        Point qx=(Point) q.getX().duplicate();
        Point qy=(Point) q.getY().duplicate();

        Point rz=(Point) qx.duplicate().sub(tx).twice();
        Element temp0=rz.duplicate().mul(p.getY().toBigInteger()).twice();
        Element temp1=qy.duplicate().add(ty).mul(p.getX().toBigInteger()).mul(4).negate();
        Element temp2=qy.duplicate().mul(qx).sub(ty).mul(qx).mul(4).sub(qy.duplicate().mul(rz).twice());

        Polynomial result=(Polynomial) Fp12.newRandomElement();
        result.getCoefficient(0).set(temp0);
        result.getCoefficient(1).set(temp1);
        result.getCoefficient(2).set(0);
        result.getCoefficient(3).set(temp2);
        result.getCoefficient(4).set(0);
        result.getCoefficient(5).set(0);
        return result;
    }

    public Element linetq2(Point t,Point q,Point p,PolyModField Fp12){
        Element Fp2One =t.getX().getField().newOneElement();
        Element Fp2Onepw3 =Fp2One.duplicate().pow(BigInteger.valueOf(3));


        Element zr=q.getX().duplicate().mul(Fp2One.duplicate().square()).sub(t.getX()).mul(Fp2One).twice();

        Element t1=zr.duplicate().mul(p.getY().toBigInteger()).twice();
        Element t2=q.getY().duplicate().mul(Fp2One.duplicate().pow(BigInteger.valueOf(3))).sub(t.getY()).mul(p.getX().duplicate().toBigInteger().multiply(BigInteger.valueOf(4))).negate();
        Element t3=q.getY().duplicate().mul(Fp2Onepw3).sub(t.getY());
        t3.mul(q.getX()).mul(4);
        Element t4=q.getY().duplicate().mul(zr).twice().negate();
        t3.add(t4);
        Polynomial result=Fp12.newElement();
        result.getCoefficient(0).set(t1);
        result.getCoefficient(1).set(t2);
        result.getCoefficient(2).set(0);
        result.getCoefficient(3).set(t3);
        result.getCoefficient(4).set(0);
        result.getCoefficient(5).set(0);
        return result;
    }
    public Element linerq(Point r,Point q,Point p,PolyModField Fp12){
        Element Fp2One=q.getX().getField().newOneElement();

        Element t0=q.getX().duplicate().mul(Fp2One.duplicate().square());
        Element t1=q.getY().duplicate().add(Fp2One).square().sub(q.getY().duplicate().square()).sub(Fp2One.duplicate().square());
        t1.mul(Fp2One.duplicate().square());
        Element t2=t0.duplicate().sub(r.getX());
        Element t3=t2.duplicate().square();
        Element t4=t3.duplicate().mul(4);
        Element t5=t4.duplicate().mul(t2);
        Element t6=t1.duplicate().sub(r.getY().duplicate().twice());
        Element t9=t6.duplicate().mul(q.getX());
        Element t7=r.getX().duplicate().mul(t4);
        Element xt=t6.duplicate().square().sub(t5).sub(t7.duplicate().twice());
        Element zt=Fp2One.duplicate().add(t2).square().sub(Fp2One.duplicate().square()).sub(t3);

        Element t10=q.getY().duplicate().add(zt);
        Element t8=t7.duplicate().sub(xt).mul(t6);
        t0=r.getY().duplicate().mul(t5).twice();
        Element yt=t8.duplicate().sub(t0);
        t10=t10.duplicate().square().sub(q.getY().duplicate().square()).sub(zt.duplicate().square());
        t9=t9.duplicate().twice().sub(t10);
        t10=zt.duplicate().mul(p.getY().toBigInteger()).twice();
        t6.negate();
        t1=t6.duplicate().mul(p.getX().toBigInteger()).twice();

        Polynomial l=(Polynomial) Fp12.newRandomElement();
        l.getCoefficient(0).set(t10);
        l.getCoefficient(1).set(t1);
        l.getCoefficient(2).set(0);
        l.getCoefficient(3).set(t9);
        l.getCoefficient(4).set(0);
        l.getCoefficient(5).set(0);
        return l;
    }


    /**
     *q is point in Fp2
     */

    public Point fob(Point q,BigInteger p){
        Element t1=q.getX().duplicate().pow(p);
        Element t2=q.getY().duplicate().pow(p);

        Point result=(Point) q.getField().newRandomElement();
        result.getX().set(t1);
        result.getY().set(t2);
        return result;
    }
    public Point fob2(Point q,BigInteger p){
        Element t1=q.getX().duplicate().pow(p.pow(2));
        Element t2=q.getY().duplicate().pow(p.pow(2));

        Point result=(Point) q.getField().newRandomElement();
        result.getX().set(t1);
        result.getY().set(t2);
        return result;
    }

}