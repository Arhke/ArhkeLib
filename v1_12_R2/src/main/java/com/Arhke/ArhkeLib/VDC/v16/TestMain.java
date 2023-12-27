//package com.Arhke.ArhkeLib.VDC.v16;
//
//public class TestMain {
//    public static void main(String[] args){
//        double rawDamage = 20, rawToughness = 2t0;
//        float damage = (float)rawDamage, toughness = (float)rawToughness;
//        System.out.println((1f- clamp((damage*toughness-200)/(damage*toughness+0.0000001), 0f,0.8f)));
//        damage*=(1f-clamp((damage*toughness-200)/(damage*toughness+0.0000001), 0f,0.8f));
//        System.out.println(damage);
//    }
//    public static float clamp(float var0, float var1, float var2) {
//        if (var0 < var1) {
//            return var1;
//        } else {
//            return var0 > var2 ? var2 : var0;
//        }
//    }
//    public static double clamp(double var0, double var2, double var4) {
//        if (var0 < var2) {
//            return var2;
//        } else {
//            return var0 > var4 ? var4 : var0;
//        }
//    }
//}
