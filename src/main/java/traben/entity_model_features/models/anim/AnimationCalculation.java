package traben.entity_model_features.models.anim;

import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.Angerable;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.FoxEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.dimension.DimensionTypes;
import traben.entity_model_features.EMFData;
import traben.entity_model_features.models.EMF_EntityModel;
import traben.entity_model_features.models.EMF_ModelPart;
import traben.entity_model_features.models.anim.EMFParser.MathComponent;
import traben.entity_model_features.models.anim.EMFParser.MathExpression;
import traben.entity_model_features.utils.EMFUtils;

import java.util.UUID;

public class AnimationCalculation {


    public int indentCount = 0;
    MathComponent EMFCalculator;

    public Entity getEntity() {
        return entity;
    }

     public float getDimension() {
        if(entity == null || entity.getWorld() == null) return 0;
        Identifier id = entity.getWorld().getDimensionKey().getValue();
        if(id.equals(DimensionTypes.THE_NETHER_ID))return -1;
        if(id.equals(DimensionTypes.THE_END_ID))return 1;
        return 0;
    }

    public float getPlayerX(){
        return MinecraftClient.getInstance().player == null ? 0: (float) MathHelper.lerp(tickDelta, MinecraftClient.getInstance().player.prevX,MinecraftClient.getInstance().player.getX());
    }
    public float getPlayerY(){
        return MinecraftClient.getInstance().player == null ? 0: (float) MathHelper.lerp(tickDelta, MinecraftClient.getInstance().player.prevY,MinecraftClient.getInstance().player.getY());
    }
    public float getPlayerZ(){
        return MinecraftClient.getInstance().player == null ? 0: (float) MathHelper.lerp(tickDelta, MinecraftClient.getInstance().player.prevZ,MinecraftClient.getInstance().player.getZ());
    }
    public float getPlayerRX(){
        return (MinecraftClient.getInstance().player == null) ? 0 :
                (float) Math.toRadians(MathHelper.lerpAngleDegrees(tickDelta, MinecraftClient.getInstance().player.prevPitch,MinecraftClient.getInstance().player.getPitch()));
    }
    public float getPlayerRY(){
        return (MinecraftClient.getInstance().player == null) ? 0 :
                (float) Math.toRadians(MathHelper.lerpAngleDegrees(tickDelta, MinecraftClient.getInstance().player.prevYaw,MinecraftClient.getInstance().player.getYaw()));
    }
    public float getEntityX(){
        return getEntity() == null ? 0: (float) MathHelper.lerp (getTickDelta(),getEntity().prevX ,getEntity().getX());
    }
    public float getEntityY(){
        return getEntity() == null ? 0:
                //(float) getEntity().getY();
                (float) MathHelper.lerp (getTickDelta(),getEntity().prevY ,getEntity().getY());
    }
    public float getEntityZ(){
        return getEntity() == null ? 0: (float) MathHelper.lerp (getTickDelta(),getEntity().prevZ ,getEntity().getZ());
    }
    public float getEntityRX(){
        return (getEntity() == null) ? 0 :
                //(float) Math.toRadians(getEntity().getPitch(tickDelta));
                (float) Math.toRadians(MathHelper.lerpAngleDegrees(tickDelta, getEntity().prevPitch,getEntity().getPitch()));
    }
    public float getEntityRY(){
        return (getEntity() instanceof LivingEntity alive) ?
                (float) Math.toRadians(MathHelper.lerpAngleDegrees(tickDelta, alive.prevBodyYaw,alive.getBodyYaw()) ) : 0;
    }

    //long changed to float... should be fine tbh
    public float getTime() {
        return entity == null || entity.getWorld() == null ? 0 : entity.getWorld().getTime() + tickDelta;
    }



    public float getHealth() {
        return entity instanceof LivingEntity alive ? alive.getHealth() : 1;
    }
    public float getDeathTime() {
        return entity instanceof LivingEntity alive ? alive.deathTime : 0;
    }
    public float getAngerTime() {
        return entity == null || !(entity instanceof Angerable) ? 0 : ((Angerable)entity).getAngerTime();
    }
    public float getMaxHealth() {
        return entity instanceof LivingEntity alive ? alive.getMaxHealth() : 1;
    }
    public float getId() {
        return entity == null ? 0 : entity.getUuid().hashCode();
    }


    public float getHurtTime() {
        return entity instanceof LivingEntity alive ? alive.hurtTime : 0;
    }
    public boolean isInWater() {
        return entity != null && entity.isTouchingWater();
    }
    public boolean isBurning() {
        return entity != null && entity.isOnFire();
    }
    public boolean isRiding() {
        return parentModel.riding;
       // return entity != null && entity.hasVehicle();
    }

    public boolean isChild() {
        return entity instanceof LivingEntity alive && alive.isBaby();
    }
    public boolean isOnGround() {
        return entity != null && entity.isOnGround();
    }
    public boolean isAlive() {
        return entity != null && entity.isAlive();
    }
    public boolean isAggressive() {return entity instanceof MobEntity && ((MobEntity) entity).isAttacking();}


    public boolean isGlowing() {
        return entity != null && entity.isGlowing();
    }
    public boolean isHurt() {return entity instanceof LivingEntity alive && alive.hurtTime > 0;}
    public boolean isInHand() {return false;}
    public boolean isInItemFrame() {
        return false;
    }
    public boolean isInGround() {return entity instanceof ArrowEntity arrow && arrow.isOnGround();}
    public boolean isInGui() {
        return false;
    }
    public boolean isInLava() {
        return entity != null && entity.isInLava();
    }
    public boolean isInvisible() {
        return entity != null && entity.isInvisible();
    }
    public boolean isOnHead() {
        return false;
    }
    public boolean isOnShoulder() {
        return false;
    }
    public boolean isRidden() {return entity != null && entity.hasPassengers();}
    public boolean isSitting() {
        return entity != null && (
                entity instanceof TameableEntity tame && tame.isSitting() ||
                        entity instanceof FoxEntity fox && fox.isSitting()

        );
    }
    public boolean isSneaking() {
        return entity != null && entity.isSneaking();
    }
    public boolean isSprinting() {
        return entity != null && entity.isSprinting();
    }
    public boolean isTamed() {return entity != null && entity instanceof TameableEntity tame && tame.isTamed();}
    public boolean isWet() {
        return entity != null && entity.isWet();
    }
    public float getSwingProgress() {
        return  entity instanceof LivingEntity alive ? alive.getHandSwingProgress(tickDelta) : 0;
    }

    public float getAge() {
         //return entity == null ? 0 : entity.age + tickDelta;
        return animationProgress;
    }

    public float getLimbAngle() {
        return limbAngle;
    }

    public float getLimbDistance() {
        return limbDistance;
    }


    public float getHeadYaw() {
        return headYaw;
    }

    public float getHeadPitch() {
        return headPitch;

    }

    public float getTickDelta() {
        return tickDelta;
    }

    Entity entity;
    float limbAngle=0;
    float limbDistance=0;
    float animationProgress=0;
    float headYaw=0;
    float headPitch=0;
    float tickDelta=0;

     public EMF_ModelPart modelPart = null;
     public ModelPart vanillaModelPart = null;

     public final EMF_EntityModel<?> parentModel;
     public final AnimVar varToChange;
     public final String animKey;


     final float defaultValue;


    public AnimationCalculation(EMF_EntityModel<?> parent, ModelPart part, AnimVar varToChange, String animKey, String initialExpression) {

        this.animKey = animKey;
        isVariable =animKey.startsWith("var");
        this.parentModel = parent;
        this.varToChange = varToChange;
        if(part instanceof EMF_ModelPart emf)
            this.modelPart = emf;
        else
            this.vanillaModelPart = part;

        if(varToChange != null) {
            resultIsAngle = (varToChange == AnimVar.rx || varToChange == AnimVar.ry ||varToChange == AnimVar.rz);
            defaultValue = varToChange.getDefaultFromModel(part);
            if(this.modelPart != null)
                varToChange.setValueAsAnimated(this.modelPart);
        } else {
            defaultValue = 0;
        }
        prevResults.defaultReturnValue(defaultValue);
        prevPrevResults.defaultReturnValue(defaultValue);

        EMFCalculator = MathExpression.getOptimizedExpression(initialExpression,false, this);


    }

    public final boolean isVariable;

    private boolean resultIsAngle = false;
    public boolean verboseMode = false;

    public void setVerbose(boolean val) {
        verboseMode = val;
    }





    public float getResultInterpolateOnly(LivingEntity entity0){
        if(vanillaModelPart != null){
            return varToChange.getFromVanillaModel(vanillaModelPart);
        }
        if(entity0 == null) {
            if(EMFData.getInstance().getConfig().printModelCreationInfoToLog) System.out.println("entity was null for getResultOnly, (okay for model init)");
            return 0;
        }

        UUID id = entity0.getUuid();
        if(resultIsAngle){
            return MathHelper.lerpAngleDegrees(parentModel.currentAnimationDeltaForThisTick,prevPrevResults.getFloat(id), prevResults.getFloat(id));
        }
        return MathHelper.lerp(parentModel.currentAnimationDeltaForThisTick,prevPrevResults.getFloat(id), prevResults.getFloat(id));

    }

    public float getLastResultOnly(LivingEntity entity0){

        if(vanillaModelPart != null){
            return varToChange.getFromVanillaModel(vanillaModelPart);
        }
        if(entity0 == null) {
            if(EMFData.getInstance().getConfig().printModelCreationInfoToLog) System.out.println("entity was null for getLastResultOnly, (okay for model init)");
            return 0;
        }

       return prevResults.getFloat(entity0.getUuid());

    }
    public float getResultViaCalculate(LivingEntity entity0, float limbAngle0, float limbDistance0,
                                       float animationProgress0, float headYaw0, float headPitch0, float tickDelta0, boolean storeResult){

        if(vanillaModelPart != null){
            return varToChange.getFromVanillaModel(vanillaModelPart);
        }
        if(entity0 == null) {
            if(EMFData.getInstance().getConfig().printModelCreationInfoToLog) System.out.println("entity was null for getResultOnly, (okay for model init)");
            return 0;
        }

        UUID id = entity0.getUuid();

            entity = entity0;
            limbAngle = limbAngle0;
            limbDistance = limbDistance0;
            animationProgress = animationProgress0;
            headYaw = headYaw0;
            headPitch = headPitch0;
            tickDelta = tickDelta0;

            float result = calculatorRun();

            float oldResult = prevResults.getFloat(id);
            if(storeResult) {
                prevPrevResults.put(id, oldResult);
                prevResults.put(id, result);
            }
            return oldResult;
    }

    public float getResultViaCalculate(LivingEntity entity0, float limbAngle0, float limbDistance0,
                                       float animationProgress0, float headYaw0, float headPitch0, float tickDelta0){
        return  getResultViaCalculate(entity0, limbAngle0, limbDistance0, animationProgress0, headYaw0, headPitch0, tickDelta0,true);
    }



    public float calculatorRun() {
//        try {
            if (EMFData.getInstance().getConfig().printAllMaths) {
                setVerbose(true);
                float val = EMFCalculator.get();
                EMFUtils.EMF_modMessage("animation result: " + animKey + " = " + val);
                return val;
            } else {
                return EMFCalculator.get();
            }
//        }catch(MathComponent.EMFMathException e){
//            return Float.NaN;
//        }

    }



    Object2FloatOpenHashMap<UUID> prevPrevResults = new Object2FloatOpenHashMap<>();
     public Object2FloatOpenHashMap<UUID> prevResults = new Object2FloatOpenHashMap<>();



    public void calculateAndSet(LivingEntity entity0, float limbAngle0, float limbDistance0, float animationProgress0, float headYaw0, float headPitch0, float tickDelta0){
        if (parentModel.calculateForThisAnimationTick) {
            handleResult(getResultViaCalculate(entity0,  limbAngle0,  limbDistance0,  animationProgress0,  headYaw0,  headPitch0,  tickDelta0));
        }else if (!isVariable){
            handleResult(getResultInterpolateOnly(entity0));
        }
    }

    private void handleResult(float result){
        if(Float.isNaN(result)){
            if(varToChange != null)
                varToChange.set(modelPart, Float.MAX_VALUE);
        }else if(modelPart != null){
            varToChange.set(modelPart, result);
        }
    }


    public void animPrint(String str){
        StringBuilder indent = new StringBuilder();
        for (int i = 0; i < indentCount; i++) {
            indent.append("> ");
        }
        System.out.println(indent+ str);
    }










    public enum AnimVar {
        tx,ty,tz,
        rx,ry,rz,
        sx,sy,sz,
        visible,
        visible_boxes,
        CUSTOM();


        public void set(EMF_ModelPart part, Float value) {
            if (value == null){
                System.out.println("this model couldn't be set as the calculation returned null: "+part.selfModelData.id+"."+this);
                return;
            }
            switch (this){
                case tx -> {
                    //part.tx = value;
                    part.setAnimPivotX(value);
                }
                case ty -> {
                    //part.ty = value;
                    part.setAnimPivotY(value);
                }
                case tz -> {
                    //part.tz = value;
                    part.setAnimPivotZ(value);
                }
                case rx -> {
                    //part.rx = value;
                    //part.pitch = value;
                    part.setAnimPitch(value);
                }
                case ry -> {
                    //part.ry = value;
                    //part.yaw = value;
                    part.setAnimYaw(value);
                }
                case rz -> {
                    //part.rz = value;
                   // part.roll = value;
                    part.setAnimRoll(value);
                }
                case sx -> {
                    //part.sx = value;
                    part.xScale = value;
                }
                case sy -> {
                    //part.sy = value;
                    part.yScale = value;
                }
                case sz -> {
                    //part.sz = value;
                    part.zScale = value;
                }
                case CUSTOM -> {//todo visibles
                    //todo pain.jpeg
                }
            }
        }
        public void setValueAsAnimated(EMF_ModelPart part) {
            if (part == null){
                System.out.println("this model couldn't be anim set as the method sent null part to "+this);
                return;
            }
            switch (this){
                case tx -> {
                    part.doesAnimtx = true;
                }
                case ty -> {
                    part.doesAnimty = true;
                }
                case tz -> {
                    part.doesAnimtz = true;
                }
                case rx -> {
                    part.doesAnimrx = true;
                }
                case ry -> {
                    part.doesAnimry = true;
                }
                case rz -> {
                    part.doesAnimrz = true;
                }
                case sx -> {
                    part.doesAnimsx = true;
                }
                case sy -> {
                    part.doesAnimsy = true;
                }
                case sz -> {
                    part.doesAnimsz = true;
                }
                default-> {//todo visibles
                    //hmmm
                }
            }
        }
        public float getFromEMFModel(EMF_ModelPart modelPart) {
            return getFromEMFModel(modelPart,false);
        }
        public float getFromEMFModel(EMF_ModelPart modelPart, boolean isSibling) {
            if(modelPart == null){
                System.out.println("EMF model part was null cannot get its value");
                return 0;
            }
            switch (this){
                case tx -> {
                    //return modelPart.tx.floatValue();
                    //sibling check is required to remove parent offsets if they are from the same parent
                    //todo this might actually be required on every single get call to a parent num == 1 part, i have only seen the issue on parts matching parents, check this
                    return isSibling ? modelPart.getAnimPivotXSibling() : modelPart.getAnimPivotX();
                }
                case ty -> {
                    //return modelPart.ty.floatValue();
                    return isSibling ? modelPart.getAnimPivotYSibling() : modelPart.getAnimPivotY();
                }
                case tz -> {
                    //return modelPart.tz.floatValue();
                    return isSibling ? modelPart.getAnimPivotZSibling() : modelPart.getAnimPivotZ();
                }
                case rx -> {
                    //return modelPart.rx.floatValue();
                    return modelPart.pitch;
                }
                case ry -> {
                    //return modelPart.ry.floatValue();
                    return modelPart.yaw;
                }
                case rz -> {
                    //return modelPart.rz.floatValue();
                    return modelPart.roll;
                }
                case sx -> {
                    //return modelPart.sx.floatValue();
                    return modelPart.xScale;
                }
                case sy -> {
                    //return modelPart.sy.floatValue();
                    return modelPart.yScale;
                }
                case sz -> {
                    //return modelPart.sz.floatValue();
                    return modelPart.zScale;
                }
                case visible, visible_boxes -> {//todo
                    //return modelPart.sz.floatValue();
                    return modelPart.visible? 1:0;
                }
                default -> {
                    System.out.println("model variable was defaulted cannot get its value");
                    return 0;
                }
            }
        }
        public float getDefaultFromModel(ModelPart modelPart){
            if(modelPart == null){
                System.out.println("model part was null cannot get its default value");
                return 0;
            }
            ModelTransform transform = modelPart.getDefaultTransform();
            switch (this){
                case tx -> {
                    return transform.pivotX;
                }
                case ty -> {
                    return transform.pivotY;
                }
                case tz -> {
                    return transform.pivotZ;
                }
                case rx -> {
                    return transform.pitch;
                }
                case ry -> {
                    return transform.yaw;
                }
                case rz -> {
                    return transform.roll;
                }
                case sx, sz, sy -> {
                    if(modelPart instanceof EMF_ModelPart emf)
                        return emf.selfModelData.scale;
                    else
                        return 1;
                }
                case visible, visible_boxes -> {
                    return 1;//todo
                }
                default -> {
                    System.out.println("model variable was defaulted cannot get its default value");
                    return 0;
                }
            }
        }

        public float getFromVanillaModel(ModelPart modelPart) {
            if(modelPart == null){
                System.out.println("model part was null cannot get its value");
                return 0;
            }
            switch (this){
                case tx -> {
                    return modelPart.pivotX;
                }
                case ty -> {
                    return modelPart.pivotY;
                }
                case tz -> {
                    return modelPart.pivotZ;
                }
                case rx -> {
                    return modelPart.pitch;
                }
                case ry -> {
                    return modelPart.yaw;
                }
                case rz -> {
                    return modelPart.roll;
                }
                case sx -> {
                    return modelPart.xScale;
                }
                case sy -> {
                    return modelPart.yScale;
                }
                case sz -> {
                    return modelPart.zScale;
                }
                case visible, visible_boxes -> {//todo
                    return modelPart.visible ? 1 : 0;
                }
                default -> {
                    System.out.println("model variable was defaulted cannot get its value");
                    return 0;
                }
            }
        }
//        public void setValueInVanillaModel(ModelPart modelPart, Double value) {
//            if(modelPart == null){
//                System.out.println("model part was null cannot set its value");
//                return;
//            }
//            switch (this){
//                case tx -> {
//                     modelPart.pivotX = value.floatValue();
//                }
//                case ty -> {
//                     modelPart.pivotY = value.floatValue();
//                }
//                case tz -> {
//                     modelPart.pivotZ = value.floatValue();
//                }
//                case rx -> {
//                     modelPart.pitch = value.floatValue();
//                }
//                case ry -> {
//                     modelPart.yaw = value.floatValue();
//                }
//                case rz -> {
//                     modelPart.roll = value.floatValue();
//                }
//                case sx -> {
//                     modelPart.xScale = value.floatValue();
//                }
//                case sy -> {
//                     modelPart.yScale = value.floatValue();
//                }
//                case sz -> {
//                     modelPart.zScale = value.floatValue();
//                }
//                default -> {
//                    System.out.println("model variable was defaulted cannot set its value");
//                }
//            }
//        }
    }
}
