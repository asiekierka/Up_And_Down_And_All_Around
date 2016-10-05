package uk.co.mysterymayhem.gravitymod.asm;

import net.minecraftforge.fml.common.FMLLog;
import org.objectweb.asm.ClassReader;
import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;
//import static uk.co.mysterymayhem.gravitymod.asm.ObfuscationHelper.DeobfAwareString;
//import static uk.co.mysterymayhem.gravitymod.asm.ObfuscationHelper.FieldInstruction;
//import static uk.co.mysterymayhem.gravitymod.asm.ObfuscationHelper.IDeobfAware;
//import static uk.co.mysterymayhem.gravitymod.asm.ObfuscationHelper.IDeobfAwareClass;
//import static uk.co.mysterymayhem.gravitymod.asm.ObfuscationHelper.PrimitiveClassName;
//import static uk.co.mysterymayhem.gravitymod.asm.ObfuscationHelper.ObjectClassName;

import java.util.HashMap;
import java.util.ListIterator;
import java.util.function.Function;

/**
 * Created by Mysteryem on 2016-08-16.
 */
public class Transformer implements IClassTransformer {

//    private static final IDeobfAware INIT = new DeobfAwareString("<init>");
//    private static final IDeobfAwareClass VOID = new PrimitiveClassName("V");
//    private static final IDeobfAwareClass CHAR = new PrimitiveClassName("C");
//    private static final IDeobfAwareClass DOUBLE = new PrimitiveClassName("D");
//    private static final IDeobfAwareClass FLOAT = new PrimitiveClassName("F");
//    private static final IDeobfAwareClass INT = new PrimitiveClassName("I");
//    private static final IDeobfAwareClass LONG = new PrimitiveClassName("J");
//    private static final IDeobfAwareClass SHORT = new PrimitiveClassName("S");
//    private static final IDeobfAwareClass BOOLEAN = new PrimitiveClassName("Z");

    private static final HashMap<String, Function<byte[], byte[]>> classNameToMethodMap = new HashMap<>();
    private static final String classToReplace = "net/minecraft/entity/player/EntityPlayer";
    private static final String classReplacement = "uk/co/mysterymayhem/gravitymod/asm/EntityPlayerWithGravity";

    static {
        //classNameToMethodMap.put("net.minecraft.entity.player.EntityPlayer", Transformer::patchEntityPlayerClass);
        //classNameToMethodMap.put("net.minecraft.entity.player.EntityPlayerSP", Transformer::patchEntityPlayerSPClass);
        classNameToMethodMap.put("net.minecraft.entity.player.EntityPlayerMP", Transformer::patchEntityPlayerSubClass);
        classNameToMethodMap.put("net.minecraft.client.entity.AbstractClientPlayer", Transformer::patchEntityPlayerSubClass);
        classNameToMethodMap.put("net.minecraft.client.entity.EntityPlayerSP", Transformer::patchEntityPlayerSP);
        classNameToMethodMap.put("net.minecraft.network.NetHandlerPlayServer", Transformer::patchNetHandlerPlayServer);
        classNameToMethodMap.put("net.minecraft.entity.Entity", Transformer::patchEntity);
        classNameToMethodMap.put("net.minecraft.entity.EntityLivingBase", Transformer::patchEntityLivingBase);
//        classNameToMethodMap.put("net.minecraft.client.renderer.EntityRenderer", Transformer::patchEntityRenderer);
//        classNameToMethodMap.put("net.minecraft.block.BlockChest", Transformer::patchBlockChestAndBlockCocoa);
//        classNameToMethodMap.put("net.minecraft.block.BlockCocoa", Transformer::patchBlockChestAndBlockCocoa);
//        classNameToMethodMap.put("net.minecraft.block.BlockFenceGate", Transformer::patchBlockFenceGate);
        classNameToMethodMap.put("net.minecraft.client.audio.SoundManager", Transformer::patchSoundManager);
//        classNameToMethodMap.put("net.minecraft.client.particle.ParticleManager", Transformer::patchParticleManager);
////        classNameToMethodMap.put("net.minecraft.client.renderer.RenderGlobal", Transformer::patchRenderGlobal);
//        classNameToMethodMap.put("net.minecraft.client.particle.Particle", Transformer::patchParticle);
//        classNameToMethodMap.put("net.minecraft.item.ItemBow", Transformer::patchItemBow);
//        classNameToMethodMap.put("net.minecraft.item.ItemEnderPearl", Transformer::patchThrowableMojangItem);
//        classNameToMethodMap.put("net.minecraft.item.ItemSnowBall", Transformer::patchThrowableMojangItem);
//        classNameToMethodMap.put("net.minecraft.item.ItemLingeringPotion", Transformer::patchThrowableMojangItem);
//        classNameToMethodMap.put("net.minecraft.item.ItemSplashPotion", Transformer::patchThrowableMojangItem);
//        classNameToMethodMap.put("net.minecraft.item.ItemExpBottle", Transformer::patchThrowableMojangItem);
//        classNameToMethodMap.put("net.minecraft.item.ItemEgg", Transformer::patchThrowableMojangItem);
//        classNameToMethodMap.put("net.minecraft.entity.projectile.EntityFishHook", Transformer::patchEntityFishHook);
//        classNameToMethodMap.put("net.minecraft.client.renderer.EntityRenderer", Transformer::patchEntityRenderer2);

        classNameToMethodMap.put("net.minecraft.entity.EntityBodyHelper", Transformer::patchEntityBodyHelper);
        classNameToMethodMap.put("net.minecraft.client.renderer.entity.RenderLivingBase", Transformer::patchRenderLivingBase);
    }

    private static void log(String string, Object... objects) {
        FMLLog.info("[UpAndDown] " + string, objects);
    }


    /*Entity.moveRelative

    Does water and flight movement apparently, not as important to get this working

    Could add a method override in EntityPlayer for EntityPlayer.moveRelative instead that calls super.moveRelative (less conflicts!)
     */

    /*Entity.moveEntity

    Take the input x, y and z motion and adjust ('rotate') it for the gravity direction of the entity, then run the rest of the method unchanged

    Could add a method override in EntityPlayer for EntityPlayer.moveEntity instead that calls super.moveEntity (less conflicts!)

    This could be a good place to set entity.onGround = true/false; dependent on the gravity direction
     */

    /*EntityPlayer.getLook(float partialTicks)

    Need to rotate the returned Vec3d to take into account how we've rotated the player's camera according to their gravity
     */

    /*EntityPlayerSP.func_189810_i
     */


    @Override
    public byte[] transform(String className, String transformedClassName, byte[] bytes) {
        if (bytes == null) {
            return null;
        }

        Function<byte[], byte[]> function = classNameToMethodMap.get(transformedClassName);

        if (function == null) {
//            if (!transformedClassName.startsWith("net.minecraft") && !transformedClassName.startsWith("uk.co.mysterymayhem.gravitymod")) {
//                //return patchGetRotations(bytes)
//            }

            return bytes;
        } else {
            log("Patching %s", className);
//            System.out.println("[UpAndDownAndAllAround] Patching " + className);
//            byte[] toReturn = function.apply(bytes);
            return function.apply(bytes);
//            System.out.println("[UpAndDownAndAllAround] Patched " + className);
        }
    }

    private static byte[] patchEntityPlayerSubClass(byte[] bytes) {
        ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(bytes);
        classReader.accept(classNode, 0);

        classNode.superName = classReplacement;

        for (MethodNode methodNode : classNode.methods) {

            for (ListIterator<AbstractInsnNode> iterator = methodNode.instructions.iterator(); iterator.hasNext(); ) {
                AbstractInsnNode next = iterator.next();
                switch (next.getType()) {
                    case (AbstractInsnNode.TYPE_INSN):
                        TypeInsnNode typeInsnNode = (TypeInsnNode) next;
                        if (typeInsnNode.desc.equals(classToReplace)) {
                            typeInsnNode.desc = classReplacement;
                        }
                        break;
                    case (AbstractInsnNode.FIELD_INSN):
                        FieldInsnNode fieldInsnNode = (FieldInsnNode) next;
                        if (fieldInsnNode.owner.equals(classToReplace)) {
                            fieldInsnNode.owner = classReplacement;
                        }
                        break;
                    case (AbstractInsnNode.METHOD_INSN):
                        MethodInsnNode methodInsnNode = (MethodInsnNode) next;
                        if (methodInsnNode.owner.equals(classToReplace)) {
                            methodInsnNode.owner = classReplacement;
                        }
                        break;
                    default:
                        break;
                }
            }
//            while (iterator.hasNext()){
//
//                iterator.next();
//            }
        }

        log("Injected super class into " + classNode.name);

        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        classNode.accept(classWriter);
        return classWriter.toByteArray();
    }

    private static byte[] patchRenderLivingBase(byte[] bytes) {
        ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(bytes);
        classReader.accept(classNode, 0);

        for (MethodNode methodNode : classNode.methods) {
            if (methodNode.name.equals("doRender")) {
                patchMethodUsingAbsoluteRotations(methodNode, ALL_GET_ROTATION_VARS);
                break;
            }
        }

        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        classNode.accept(classWriter);
        return classWriter.toByteArray();
    }



    private static byte[] patchEntityRenderer2(byte[] bytes) {
        ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(bytes);
        classReader.accept(classNode, 0);

        for (MethodNode methodNode : classNode.methods) {
            if (methodNode.name.equals("orientCamera")) {
                patchMethodUsingAbsoluteRotations(methodNode, ALL_GET_ROTATION_VARS);
                break;
            }
        }

        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        classNode.accept(classWriter);
        return classWriter.toByteArray();
    }

    private static byte[] patchEntityRenderer(byte[] bytes) {
        ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(bytes);
        classReader.accept(classNode, 0);

        boolean WorldClient$rayTraceBlocks_found = false;

        for (MethodNode methodNode : classNode.methods) {
            if (methodNode.name.equals("orientCamera")) {
//                ArrayList<LocalVariableNode> localVarsCopy = new ArrayList<>(methodNode.localVariables);
//                localVarsCopy.sort((o1, o2) -> Integer.compare(o1.index, o2.index));
//                localVarsCopy.forEach(localVariableNode -> {
//                    log("name: " + localVariableNode.name + ", desc: " + localVariableNode.desc + ", index: " + localVariableNode.index);
//                });

                boolean foundEntityVar = false;
                int entityVar = -1;
                for (ListIterator<AbstractInsnNode> iterator = methodNode.instructions.iterator(); iterator.hasNext() && !WorldClient$rayTraceBlocks_found; ) {
                    AbstractInsnNode next = iterator.next();
                    if(!foundEntityVar && next instanceof VarInsnNode) {
                        VarInsnNode varInsnNode = (VarInsnNode)next;
                        if (varInsnNode.getOpcode() == Opcodes.ASTORE) {
                            entityVar = varInsnNode.var;
                            foundEntityVar = true;
                        }
                    }
                    else if (foundEntityVar && next instanceof MethodInsnNode) {
                        MethodInsnNode methodInsnNode = (MethodInsnNode)next;
                        if (methodInsnNode.getOpcode() == Opcodes.INVOKEVIRTUAL
                                && methodInsnNode.owner.equals("net/minecraft/client/multiplayer/WorldClient")
                                && methodInsnNode.name.equals("rayTraceBlocks")
                                && methodInsnNode.desc.equals("(Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/util/math/Vec3d;)Lnet/minecraft/util/math/RayTraceResult;")) {
                            int index = 0;
                            //y, z, x
                            int[] localVariables = new int[3];
                            AbstractInsnNode nodeToInsertAfter = null;

                            while(index < 3) {
                                AbstractInsnNode previous = iterator.previous();
                                if (previous instanceof VarInsnNode) {
                                    VarInsnNode varInsnNode = (VarInsnNode)previous;
                                    if (varInsnNode.getOpcode() == Opcodes.DSTORE) {
                                        localVariables[index] = varInsnNode.var;
                                        if (index == 0) {
                                            nodeToInsertAfter = previous;
                                        }
                                        index++;
                                    }
                                }
                            }
                            while(!WorldClient$rayTraceBlocks_found) {
                                next = iterator.next();
                                if (next == nodeToInsertAfter) {
                                    iterator.add(new VarInsnNode(Opcodes.ALOAD, entityVar)); //this.entity
                                    iterator.add(new VarInsnNode(Opcodes.DLOAD, localVariables[2])); //x
                                    iterator.add(new VarInsnNode(Opcodes.DLOAD, localVariables[0])); //y
                                    iterator.add(new VarInsnNode(Opcodes.DLOAD, localVariables[1])); //z
                                    iterator.add(new MethodInsnNode(Opcodes.INVOKESTATIC,
                                            "uk/co/mysterymayhem/gravitymod/asm/Hooks",
                                            "adjustXYZ",
                                            "(Lnet/minecraft/entity/Entity;DDD)[D",
                                            false));

                                    iterator.add(new InsnNode(Opcodes.DUP));
                                    iterator.add(new InsnNode(Opcodes.DUP));

                                    iterator.add(new InsnNode(Opcodes.ICONST_0));
                                    iterator.add(new InsnNode(Opcodes.DALOAD));
                                    iterator.add(new VarInsnNode(Opcodes.DSTORE, localVariables[2])); //x = doubles[0]

                                    iterator.add(new InsnNode(Opcodes.ICONST_2));
                                    iterator.add(new InsnNode(Opcodes.DALOAD));
                                    iterator.add(new VarInsnNode(Opcodes.DSTORE, localVariables[1])); //z = doubles[0]

                                    iterator.add(new InsnNode(Opcodes.ICONST_1));
                                    iterator.add(new InsnNode(Opcodes.DALOAD));
                                    iterator.add(new VarInsnNode(Opcodes.DSTORE, localVariables[0])); //y = doubles[0]

                                    WorldClient$rayTraceBlocks_found = true;
                                }
                            }

//                            iterator.previous();// The INVOKEVIRTUAL we just found
//                            iterator.add(new VarInsnNode(Opcodes.ALOAD, 2));// Load the Entity
//                            iterator.add(new MethodInsnNode(Opcodes.INVOKESTATIC,
//                                    "uk/co/mysterymayhem/gravitymod/asm/Hooks",
//                                    "adjustVec",
//                                    "(Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/entity/Entity;)Lnet/minecraft/util/math/Vec3d;",
//                                    false));
//                            WorldClient$rayTraceBlocks_found = true;
                        }
                    }
                }
            }
        }

        if (!WorldClient$rayTraceBlocks_found) {
            throw new RuntimeException("[UpAndDownAndAllAround] Could not find WorldClient::rayTraceBlocks in " + classNode.name + "::orientCamera");
        }

        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        classNode.accept(classWriter);
        return classWriter.toByteArray();
    }

    private static byte[] patchEntity(byte[] bytes) {
        ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(bytes);
        classReader.accept(classNode, 0);

        int numReplaced = 0;
        boolean newBlockPosFound = false;
        boolean blockPos$downFound = false;
        boolean Blocks$LADDERFound = false;

        for (MethodNode methodNode : classNode.methods) {
            if (methodNode.name.equals("moveEntity")) {
                for (ListIterator<AbstractInsnNode> iterator = methodNode.instructions.iterator(); iterator.hasNext(); ) {
                    AbstractInsnNode next = iterator.next();
                    if (next instanceof MethodInsnNode) {
                        MethodInsnNode methodInsnNode = (MethodInsnNode)next;
                        //TODO: Split into 3 if statements so it works with SRG names
                        if (methodInsnNode.getOpcode() == Opcodes.INVOKEVIRTUAL
                                && methodInsnNode.owner.equals("net/minecraft/util/math/AxisAlignedBB")
                                && methodInsnNode.name.startsWith("calculate")
                                && methodInsnNode.name.endsWith("Offset")
                                && methodInsnNode.desc.equals("(Lnet/minecraft/util/math/AxisAlignedBB;D)D")) {
                            //Simple 1:1 replacement
                            methodInsnNode.setOpcode(Opcodes.INVOKESTATIC);
                            methodInsnNode.owner = "uk/co/mysterymayhem/gravitymod/asm/Hooks";
                            //old name: calculate[XYZ]Offset
                            //new name: reverse[XYZ]Offset
                            methodInsnNode.name = methodInsnNode.name.replace("calculate", "reverse");
                            methodInsnNode.desc = "(Lnet/minecraft/util/math/AxisAlignedBB;Lnet/minecraft/util/math/AxisAlignedBB;D)D";
                            numReplaced++;
                        }
                        else if (!newBlockPosFound
                                && methodInsnNode.getOpcode() == Opcodes.INVOKESPECIAL
                                && methodInsnNode.owner.equals("net/minecraft/util/math/BlockPos")
                                && methodInsnNode.name.equals("<init>")
                                && methodInsnNode.desc.equals("(III)V")) {
                            // Starts with a simple 1:1 replacement
                            methodInsnNode.setOpcode(Opcodes.INVOKESTATIC);
                            methodInsnNode.owner = "uk/co/mysterymayhem/gravitymod/asm/Hooks";
                            methodInsnNode.name = "getImmutableBlockPosBelowEntity";
                            methodInsnNode.desc = "(Lnet/minecraft/entity/Entity;)Lnet/minecraft/util/math/BlockPos;";

                            iterator.previous(); // Our INNVOKESTATIC instruction

                            // Repeatedly remove the previous instruction until it's GETFIELD Entity.posX
                            // Remove that too
                            // If we were to call iterator.previous() again, we would get the ALOAD 0 that would normally
                            // be consumed by the GETFIELD instruction we just removed
                            // And it just so happens that our INVOKESTATIC instruction needs to have ALOAD 0 before it
                            // So we cut out a bunch of no longer used instructions and inserted our own instruction
                            // (hopefully it's fine to have local variables that are never used in bytecode)
                            for (;iterator.hasPrevious() && !newBlockPosFound;) {
                                AbstractInsnNode previous = iterator.previous();
                                if (previous instanceof FieldInsnNode) {
                                    FieldInsnNode fieldInsnNode = (FieldInsnNode)previous;
                                    if (fieldInsnNode.getOpcode() == Opcodes.GETFIELD
                                            && fieldInsnNode.owner.equals("net/minecraft/entity/Entity")
                                            && fieldInsnNode.name.equals("posX")
                                            && fieldInsnNode.desc.equals("D")) {
                                        newBlockPosFound = true;
                                    }
                                }
                                //TODO: Do the labels and line numbers need to be left in? Just the labels?
//                                if (previous instanceof LineNumberNode) {
//                                    continue;
//                                }
                                if (previous instanceof LabelNode) {
                                    continue;
                                }
                                iterator.remove();
                            }
                        }
                        else if (newBlockPosFound
                                && !blockPos$downFound) {
                            if (methodInsnNode.getOpcode() == Opcodes.INVOKEVIRTUAL
                                    && methodInsnNode.owner.equals("net/minecraft/util/math/BlockPos")
                                    && methodInsnNode.name.equals("down")
                                    && methodInsnNode.desc.equals("()Lnet/minecraft/util/math/BlockPos;")) {
                                methodInsnNode.setOpcode(Opcodes.INVOKESTATIC);
                                methodInsnNode.owner = "uk/co/mysterymayhem/gravitymod/asm/Hooks";
                                methodInsnNode.name = "getRelativeDownBlockPos";
                                methodInsnNode.desc = "(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/entity/Entity;)Lnet/minecraft/util/math/BlockPos;";
                                iterator.previous();
                                VarInsnNode aload0 = new VarInsnNode(Opcodes.ALOAD, 0);
                                iterator.add(aload0);
                                blockPos$downFound = true;
                            }
                        }
                    }
                    else if (next instanceof FieldInsnNode
                            && newBlockPosFound && blockPos$downFound
                            /*&& !Blocks$LADDERFound*/) {
                        FieldInsnNode fieldInsnNode = (FieldInsnNode)next;
                        if (!Blocks$LADDERFound
                                && fieldInsnNode.getOpcode() == Opcodes.GETSTATIC
                                && fieldInsnNode.owner.equals("net/minecraft/init/Blocks")
                                && fieldInsnNode.name.equals("LADDER")
                                && fieldInsnNode.desc.equals("Lnet/minecraft/block/Block;")){
                            iterator.previous(); // returns the same GETSTATIC instruction
                            iterator.previous(); // should be ALOAD 29

                            // Start adding instructions just before the ALOAD 29 instruction
                            /*
                              Instruction // Top of stack
                                ALOAD 0   // this
                                DLOAD 30  // this,d12
                                DLOAD 32  // this,d12,d13
                                DLOAD 34  // this,d12,d13,d14
                                INVOKESTATIC uk/co/mysterymayhem/gravitymod/asm/Hooks.inverseAdjustXYZ (Lnet/minecraft/entity/Entity;DDD)[D
                                          // doubles
                                DUP       // doubles,doubles
                                DUP       // doubles,doubles,doubles
                                ICONST_0  // doubles,doubles,doubles,0
                                DALOAD    // doubles,doubles,doubles[0]
                                DSTORE 30 // doubles,doubles
                                ICONST_1  // doubles,doubles,1
                                DALOAD    // doubles,doubles[1]
                                DSTORE 32 // doubles
                                ICONST_2  // doubles,2
                                DALOAD    // doubles[2]
                                DSTORE 34 // [empty]
                            */
                            iterator.add(new VarInsnNode(Opcodes.ALOAD, 0));
                            iterator.add(new VarInsnNode(Opcodes.DLOAD, 73));//30
                            iterator.add(new VarInsnNode(Opcodes.DLOAD, 75));//32
                            iterator.add(new VarInsnNode(Opcodes.DLOAD, 77));//34
                            iterator.add(new MethodInsnNode(Opcodes.INVOKESTATIC,
                                    "uk/co/mysterymayhem/gravitymod/asm/Hooks",
                                    "inverseAdjustXYZ",
                                    "(Lnet/minecraft/entity/Entity;DDD)[D",
                                    false));
                            iterator.add(new InsnNode(Opcodes.DUP));
                            iterator.add(new InsnNode(Opcodes.DUP));
                            iterator.add(new InsnNode(Opcodes.ICONST_0));
                            iterator.add(new InsnNode(Opcodes.DALOAD));
                            iterator.add(new VarInsnNode(Opcodes.DSTORE, 73));//30
                            iterator.add(new InsnNode(Opcodes.ICONST_1));
                            iterator.add(new InsnNode(Opcodes.DALOAD));
                            iterator.add(new VarInsnNode(Opcodes.DSTORE, 75));//32
                            iterator.add(new InsnNode(Opcodes.ICONST_2));
                            iterator.add(new InsnNode(Opcodes.DALOAD));
                            iterator.add(new VarInsnNode(Opcodes.DSTORE, 77));//34
                            Blocks$LADDERFound = true;
                        }
                    }
                }
            }
            else if (methodNode.name.equals("moveRelative")) {
                patchMethodUsingAbsoluteRotations(methodNode, GET_ROTATIONYAW);
            }
        }

        if (numReplaced > 0) {
            log("Replaced " + numReplaced +
                    " usages of AxisAlignedBB:calculate[XYZ]Offset with Hooks::reverse[XYZ]Offset in " + classNode.name + "::moveEntity");
        }
        else {
            throw new RuntimeException("[UpAndDownAndAllAround] Could not find any AxisAlignedBB:calculate[XYZ]Offset methods in " + classNode.name + "::moveEntity");
        }
        if (newBlockPosFound) {

        }
        else {
            throw new RuntimeException("[UpAndDownAndAllAround] Failed to find new instance creation of BlockPos in Entity::moveEntity");
        }
        if (blockPos$downFound) {

        }
        else {
            throw new RuntimeException("[UpAndDownAndAllAround] Failed to find usage of BlockPos::down in Entity::moveEntity");
        }
        if (Blocks$LADDERFound) {

        }
        else {
            throw new RuntimeException("[UpAndDownAndAllAround] Failed to find usage of Blocks.LADDER in Entity::moveEntity");
        }

        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
        classNode.accept(classWriter);
        return classWriter.toByteArray();
    }

    private static byte[] patchEntityBodyHelper(byte[] bytes) {
        ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(bytes);
        classReader.accept(classNode, 0);

        for (MethodNode methodNode : classNode.methods) {
            if (methodNode.name.equals("updateRenderAngles")) {
                patchMethodUsingAbsoluteRotations(methodNode, GET_ROTATIONYAW + GET_ROTATIONYAWHEAD);
                break;
            }
        }

        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        classNode.accept(classWriter);
        return classWriter.toByteArray();
    }

    private static byte[] patchEntityLivingBase(byte[] bytes) {
        ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(bytes);
        classReader.accept(classNode, 0);

        boolean BlockPos$PooledMutableBlockPos$retainFound = false;
        boolean BlockPos$PooledMutableBlockPos$setPosFound = false;
        int numEntityLivingBase$isOffsetPositionInLiquidFound = 0;
        MethodInsnNode previousisOffsetPositionInLiquidMethodInsnNode = null;
        boolean GETFIELD_limbSwingAmount_found = false;
        boolean PUTFIELD_limbSwing_found = false;
        boolean replaced_getLookVec = false;
        boolean replaced_rotationPitch = false;

        for (MethodNode methodNode : classNode.methods) {
            if (methodNode.name.equals("moveEntityWithHeading")) {
                for (ListIterator<AbstractInsnNode> iterator = methodNode.instructions.iterator(); iterator.hasNext(); ) {
                    AbstractInsnNode next = iterator.next();
                    if (next instanceof MethodInsnNode) {
                        MethodInsnNode methodInsnNode = (MethodInsnNode)next;
                        if (!replaced_getLookVec
                                && methodInsnNode.getOpcode() == Opcodes.INVOKEVIRTUAL
                                && methodInsnNode.owner.equals("net/minecraft/entity/EntityLivingBase")
                                && methodInsnNode.name.equals("getLookVec")
                                && methodInsnNode.desc.equals("()Lnet/minecraft/util/math/Vec3d;")) {
                            replaced_getLookVec = true;
                            methodInsnNode.setOpcode(Opcodes.INVOKESTATIC);
                            methodInsnNode.owner = "uk/co/mysterymayhem/gravitymod/asm/Hooks";
                            methodInsnNode.name = "getRelativeLookVec";
                            methodInsnNode.desc = "(Lnet/minecraft/entity/Entity;)Lnet/minecraft/util/math/Vec3d;";
                        }
                        else if (replaced_rotationPitch
                                && !BlockPos$PooledMutableBlockPos$retainFound
                                && methodInsnNode.getOpcode() == Opcodes.INVOKESTATIC
                                && methodInsnNode.owner.equals("net/minecraft/util/math/BlockPos$PooledMutableBlockPos")
                                && methodInsnNode.name.equals("retain")
                                && methodInsnNode.desc.equals("(DDD)Lnet/minecraft/util/math/BlockPos$PooledMutableBlockPos;")){
                            methodInsnNode.owner = "uk/co/mysterymayhem/gravitymod/asm/Hooks";
                            methodInsnNode.name = "getBlockPosBelowEntity";
                            methodInsnNode.desc = "(Lnet/minecraft/entity/Entity;)Lnet/minecraft/util/math/BlockPos$PooledMutableBlockPos;";

                            iterator.previous(); //Our replacment INVOKESTATIC

                            // Deletes everything before the INVOKESTATIC until we've reached our third ALOAD
                            int aload0foundCount = 0;
                            while(iterator.hasPrevious() && aload0foundCount < 3) {
                                AbstractInsnNode previous = iterator.previous();
                                if (previous instanceof VarInsnNode) {
                                    VarInsnNode varInsnNode = (VarInsnNode)previous;
                                    if (varInsnNode.getOpcode() == Opcodes.ALOAD
                                            && varInsnNode.var == 0) {
                                        aload0foundCount++;
                                    }
                                }
                                if (aload0foundCount < 3) {
                                    iterator.remove();
                                }
                            }
                            if (aload0foundCount == 3) {
                                BlockPos$PooledMutableBlockPos$retainFound = true;
                            }
                        }
                        else if (BlockPos$PooledMutableBlockPos$retainFound
                                && !BlockPos$PooledMutableBlockPos$setPosFound
                                && methodInsnNode.getOpcode() == Opcodes.INVOKEVIRTUAL
                                && methodInsnNode.owner.equals("net/minecraft/util/math/BlockPos$PooledMutableBlockPos")
                                && methodInsnNode.name.equals("setPos")
                                && methodInsnNode.desc.equals("(DDD)Lnet/minecraft/util/math/BlockPos$PooledMutableBlockPos;")) {
                            methodInsnNode.setOpcode(Opcodes.INVOKESTATIC);
                            methodInsnNode.owner = "uk/co/mysterymayhem/gravitymod/asm/Hooks";
                            methodInsnNode.name = "setPooledMutableBlockPosToBelowEntity";
                            methodInsnNode.desc = "(Lnet/minecraft/util/math/BlockPos$PooledMutableBlockPos;Lnet/minecraft/entity/Entity;)Lnet/minecraft/util/math/BlockPos$PooledMutableBlockPos;";

                            iterator.previous(); //Our replacement INVOKESTATIC

                            // Interestingly, we have to find the same number of ALOAD 0 instructions as the previous
                            // Deletes everything before the INVOKESTATIC until we've reached our third ALOAD
                            int aload0foundCount = 0;
                            while (iterator.hasPrevious() && aload0foundCount < 3) {
                                AbstractInsnNode previous = iterator.previous();
                                if (previous instanceof VarInsnNode) {
                                    VarInsnNode varInsnNode = (VarInsnNode)previous;
                                    if (varInsnNode.getOpcode() == Opcodes.ALOAD
                                            && varInsnNode.var == 0) {
                                        aload0foundCount++;
                                    }
                                }
                                if (aload0foundCount < 3) {
                                    iterator.remove();
                                }
                            }
                            if (aload0foundCount == 3) {
                                BlockPos$PooledMutableBlockPos$setPosFound = true;
                            }
                        }
                        else if (BlockPos$PooledMutableBlockPos$setPosFound
                                && numEntityLivingBase$isOffsetPositionInLiquidFound < 2
                                && methodInsnNode.getOpcode() == Opcodes.INVOKEVIRTUAL
                                && methodInsnNode.owner.equals("net/minecraft/entity/EntityLivingBase")
                                && methodInsnNode.name.equals("isOffsetPositionInLiquid")
                                && methodInsnNode.desc.equals("(DDD)Z")){
                            if (methodInsnNode == previousisOffsetPositionInLiquidMethodInsnNode) {
                                continue;
                            }
                            previousisOffsetPositionInLiquidMethodInsnNode = methodInsnNode;
                            int numReplacementsMade = 0;
                            while (iterator.hasPrevious() && numReplacementsMade < 2) {
                                AbstractInsnNode previous = iterator.previous();
                                if (previous instanceof FieldInsnNode) {
                                    FieldInsnNode fieldInsnNode = (FieldInsnNode)previous;
                                    if (fieldInsnNode.getOpcode() == Opcodes.GETFIELD
                                            && fieldInsnNode.owner.equals("net/minecraft/entity/EntityLivingBase")
                                            && fieldInsnNode.name.equals("posY")
                                            && fieldInsnNode.desc.equals("D")) {
                                        // Replace the GETFIELD with an INVOKESTATIC of the Hook
                                        iterator.remove();

                                        iterator.add(new MethodInsnNode(Opcodes.INVOKESTATIC,
                                                "uk/co/mysterymayhem/gravitymod/asm/Hooks",
                                                "getRelativePosY",
                                                "(Lnet/minecraft/entity/Entity;)D",
                                                false));
                                        numReplacementsMade++;
                                    }
                                }
                            }
                            if (numReplacementsMade == 2) {
                                numEntityLivingBase$isOffsetPositionInLiquidFound++;
                            }
                        }
                    }
                    else if(next instanceof FieldInsnNode) {
                        FieldInsnNode fieldInsnNode = (FieldInsnNode)next;
                        if (replaced_getLookVec
                                && !replaced_rotationPitch
                                && fieldInsnNode.getOpcode() == Opcodes.GETFIELD
                                && fieldInsnNode.owner.equals("net/minecraft/entity/EntityLivingBase")
                                && fieldInsnNode.name.equals("rotationPitch")
                                && fieldInsnNode.desc.equals("F")) {
                            replaced_rotationPitch = true;
                            iterator.remove();
                            iterator.add(new MethodInsnNode(Opcodes.INVOKESTATIC,
                                    "uk/co/mysterymayhem/gravitymod/asm/Hooks",
                                    "getRelativePitch",
                                    "(Lnet/minecraft/entity/Entity;)F",
                                    false));
                        }
                        else if (numEntityLivingBase$isOffsetPositionInLiquidFound == 2
                                && !GETFIELD_limbSwingAmount_found
                                && fieldInsnNode.getOpcode() == Opcodes.GETFIELD
                                && fieldInsnNode.owner.equals("net/minecraft/entity/EntityLivingBase")
                                && fieldInsnNode.name.equals("limbSwingAmount")
                                && fieldInsnNode.desc.equals("F")) {
                            iterator.previous(); // The GETFIELD instruction
                            iterator.previous(); // ALOAD 0
                            iterator.add(new MethodInsnNode(Opcodes.INVOKESTATIC,
                                    "uk/co/mysterymayhem/gravitymod/asm/Hooks",
                                    "makePositionRelative",
                                    "(Lnet/minecraft/entity/EntityLivingBase;)V",
                                    false));
                            iterator.add(new VarInsnNode(Opcodes.ALOAD, 0));
                            GETFIELD_limbSwingAmount_found = true;
                        }
                        else if (GETFIELD_limbSwingAmount_found
                                && !PUTFIELD_limbSwing_found
                                && fieldInsnNode.getOpcode() == Opcodes.PUTFIELD
                                && fieldInsnNode.owner.equals("net/minecraft/entity/EntityLivingBase")
                                && fieldInsnNode.name.equals("limbSwing")
                                && fieldInsnNode.desc.equals("F")) {
                            iterator.add(new VarInsnNode(Opcodes.ALOAD, 0));
                            iterator.add(new MethodInsnNode(Opcodes.INVOKESTATIC,
                                    "uk/co/mysterymayhem/gravitymod/asm/Hooks",
                                    "makePositionAbsolute",
                                    "(Lnet/minecraft/entity/EntityLivingBase;)V",
                                    false));
                            PUTFIELD_limbSwing_found = true;
                        }
                    }
                }
            }
            //No, this causes weird spinning
//            else if (methodNode.name.equals("onUpdate")) {
//                patchMethodUsingAbsoluteRotations(methodNode, GET_ROTATIONYAW + GET_ROTATIONYAWHEAD);
//            }
            //TODO: Can I use the helper method here instead of doing this manually?
            else if (methodNode.name.equals("updateDistance")) {
                for (ListIterator<AbstractInsnNode> iterator = methodNode.instructions.iterator(); iterator.hasNext(); ) {
                    AbstractInsnNode next = iterator.next();
                    if (next instanceof FieldInsnNode) {
                        FieldInsnNode fieldInsnNode = (FieldInsnNode)next;
                        if (fieldInsnNode.getOpcode() == Opcodes.GETFIELD
                                && fieldInsnNode.owner.equals("net/minecraft/entity/EntityLivingBase")
                                && fieldInsnNode.name.equals("rotationYaw")
                                && fieldInsnNode.desc.equals("F")) {
                            iterator.remove();
                            iterator.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "uk/co/mysterymayhem/gravitymod/asm/Hooks",
                                    "getRelativeYaw", "(Lnet/minecraft/entity/Entity;)F", false));
                        }
                    }
                }
            }
        }

        if (BlockPos$PooledMutableBlockPos$retainFound) {

        }
        else {
            throw new RuntimeException("");
        }
        if (BlockPos$PooledMutableBlockPos$setPosFound) {

        }
        else {
            throw new RuntimeException("");
        }
        if (numEntityLivingBase$isOffsetPositionInLiquidFound == 2) {

        }
        else {
            throw new RuntimeException("");
        }
        if (GETFIELD_limbSwingAmount_found) {

        }
        else {
            throw new RuntimeException("[UpAndDownAndAllAround] Failed to find GETFIELD this.limbSwingAmount in EntityLivingBase::moveEntityWithHeading");
        }
        if (PUTFIELD_limbSwing_found) {

        }
        else {
            throw new RuntimeException("[UpAndDownAndAllAround] Failed to find PUTFIELD this.limbSwing in EntityLivingBase::moveEntityWithHeading");
        }

        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        classNode.accept(classWriter);
        return classWriter.toByteArray();
    }

    private static byte[] patchNetHandlerPlayServer(byte[] bytes) {
        ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(bytes);
        classReader.accept(classNode, 0);

        boolean foundMoveEntity = false;
        boolean foundHandleFalling = false;

        for (MethodNode methodNode : classNode.methods) {
            if (methodNode.name.equals("processPlayer")) {
                log("Modifying NetHandlerPlayServer::processPlayer");
                for (ListIterator<AbstractInsnNode> iterator = methodNode.instructions.iterator(); iterator.hasNext(); ) {
                    AbstractInsnNode next = iterator.next();
                    if (next instanceof MethodInsnNode) {
                        MethodInsnNode methodInsnNode = (MethodInsnNode) next;
                        if (!foundMoveEntity
                                && methodInsnNode.getOpcode() == Opcodes.INVOKEVIRTUAL
                                && methodInsnNode.owner.equals("net/minecraft/entity/player/EntityPlayerMP")
                                && methodInsnNode.name.equals("moveEntity")
                                && methodInsnNode.desc.equals("(DDD)V"))
                        {
                            log("Found \"playerEntity.moveEntity(...)\"");
                            methodInsnNode.setOpcode(Opcodes.INVOKESTATIC);
                            methodInsnNode.owner = "uk/co/mysterymayhem/gravitymod/asm/Hooks";
                            methodInsnNode.name = "moveEntityAbsolute";
                            methodInsnNode.desc = "(Lnet/minecraft/entity/player/EntityPlayer;DDD)V";
                            log("Replaced with \"Hooks.moveEntityAbsolute(...)\"");
                            foundMoveEntity = true;
                        }
                        else if (!foundHandleFalling
                                && methodInsnNode.getOpcode() == Opcodes.INVOKEVIRTUAL
                                && methodInsnNode.owner.equals("net/minecraft/entity/player/EntityPlayerMP")
                                && methodInsnNode.name.equals("handleFalling")
                                && methodInsnNode.desc.equals("(DZ)V"))
                        {
                            log("Found \"playerEntity.handleFalling(...)\"");
                            iterator.previous(); // INVOKEVIRTUAL handleFalling again
                            iterator.previous(); // INVOKEVIRTUAL isOnGround
                            iterator.previous(); // ALOAD 1 (CPacketPlayer method argument)
                            next = iterator.previous(); // Should be DSUB
                            if (next.getOpcode() == Opcodes.DSUB) {
                                iterator.remove();
                                VarInsnNode dload7 = new VarInsnNode(Opcodes.DLOAD, 7);
                                iterator.add(dload7);
                                MethodInsnNode invokeStaticHook = new MethodInsnNode(Opcodes.INVOKESTATIC,
                                        "uk/co/mysterymayhem/gravitymod/asm/Hooks",
                                        "netHandlerPlayServerHandleFallingYChange",
                                        "(Lnet/minecraft/entity/player/EntityPlayerMP;DDD)D",
                                        false);
                                iterator.add(invokeStaticHook);
                                iterator.previous(); // Our newly added INVOKESTATIC
                                iterator.previous(); // Our newly added DLOAD 7
                                iterator.previous(); // DLOAD 9
                                next = iterator.previous(); // GETFIELD posY
                                if (next instanceof FieldInsnNode) {
                                    FieldInsnNode fieldInsnNode = (FieldInsnNode)next;
                                    if (fieldInsnNode.owner.equals("net/minecraft/entity/player/EntityPlayerMP")
                                            && fieldInsnNode.name.equals("posY")
                                            && fieldInsnNode.desc.equals("D"))
                                    {
                                        VarInsnNode dload3 = new VarInsnNode(Opcodes.DLOAD, 3);
                                        iterator.remove();
                                        iterator.add(dload3);
                                        foundHandleFalling = true;
                                    }
                                    else {
                                        throw new RuntimeException("[UpAndDownAndAllAround] Unexpected instruction in NetHandletPlayServer::processPlayer, expecting \"GETFIELD posY\"");
                                    }
                                }
                                else {
                                    throw new RuntimeException("[UpAndDownAndAllAround] Unexpected instruction in NetHandletPlayServer::processPlayer, expecting \"GETFIELD posY\"");
                                }
                            }
                            else {
                                throw new RuntimeException("[UpAndDownAndAllAround] Unexpected instruction in NetHandletPlayServer::processPlayer, expecting \"DSUB\"");
                            }
                        }
                    }
                }
            }
        }

        if (foundMoveEntity) {
            log("Replaced \"playerEntity.moveEntity(...)\" with \"Hooks.moveEntityAbsolute(...)\" in " + classNode.name + "::processPlayer");
        } else {
            throw new RuntimeException("Could not find \"playerEntity.moveEntity(...)\" in " + classNode.name);
        }
        if (foundHandleFalling) {
            log("Replaced \"this.playerEntity.handleFalling(this.playerEntity.posY - d3, packetIn.isOnGround());\" with \"this.playerEntity.handleFalling(Hooks.netHandlerPlayServerHandleFallingYChange(playerEntity, d0, d3, d2), packetIn.isOnGround());\" in " + classNode.name + "::processPlayer");
        } else {
            throw new RuntimeException("Could not find \"this.playerEntity.handleFalling(this.playerEntity.posY - d3, packetIn.isOnGround());\" in " + classNode.name +"::processPlayer");
        }

        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        classNode.accept(classWriter);
        return classWriter.toByteArray();
    }

    private static final boolean DEBUG_AUTO_JUMP = false;

    //TODO: Instead of changing the field, call a method in Hooks that takes the aabb as an argument and returns GravityAxisAlignedBB.getOrigin().getY() or AxisAlignedBB.minY
    private static byte[] patchEntityPlayerSP(byte[] bytes) {
        ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(bytes);
        classReader.accept(classNode, 0);

        boolean isHeadSpaceFreeModified = false, onUpdateWalkingPlayerModified = false, onLivingUpdateModified = false;

        int numReplacements = 0;

//        IDeobfAware onUpdateWalkingPlayer = new DeobfAwareString("onUpdateWalkingPlayer", "func_175161_p");
//        IDeobfAwareClass AxisAlignedBB = new ObjectClassName("net/minecraft/util/math/AxisAlignedBB");
//        IDeobfAware minY = new DeobfAwareString("minY", "field_72338_b");
//        FieldInstruction minYFieldInsn = new FieldInstruction(Opcodes.GETFIELD, AxisAlignedBB, minY, DOUBLE);
//        IDeobfAwareClass EntityPlayerSP = new ObjectClassName("net/minecraft/client/entity/EntityPlayerSP");
//        IDeobfAware posY = new DeobfAwareString("posY", "field_70163_u");
//        FieldInstruction posYFieldInsn = new FieldInstruction(Opcodes.GETFIELD, EntityPlayerSP, posY, DOUBLE);

//        IDeobfAware onLivingUpdate = new DeobfAwareString("onLivingUpdate", "func_70636_d");

        for (MethodNode methodNode : classNode.methods) {
            /*


             */
            if (methodNode.name.equals("onUpdateWalkingPlayer")) {
                log("Modifying EntityPlayerSP::onUpdateWalkingPlayer");

                for (ListIterator<AbstractInsnNode> iterator = methodNode.instructions.iterator(); iterator.hasNext(); ) {
                    AbstractInsnNode next = iterator.next();
                    if (next instanceof FieldInsnNode) {
                        FieldInsnNode fieldInsnNode = (FieldInsnNode) next;
                        if (fieldInsnNode.getOpcode() == Opcodes.GETFIELD) {
                            if (fieldInsnNode.owner.equals("net/minecraft/util/math/AxisAlignedBB")
                                    && fieldInsnNode.name.equals("minY")
                                    && fieldInsnNode.desc.equals("D")) {
                                if (iterator.hasPrevious()) {
                                    iterator.previous();
                                    AbstractInsnNode previous = iterator.previous();
                                    if (previous instanceof VarInsnNode) {
                                        VarInsnNode varInsnNode = (VarInsnNode) previous;
                                        if (varInsnNode.var == 3) {
                                                /*
                                                Replace
                                                axisalignedbb.minY
                                                with
                                                this.posY
                                                in
                                                EntityPlayerSP::onUpdateWalkingPlayer
                                                 */
                                            varInsnNode.var = 0;
                                            fieldInsnNode.owner = "net/minecraft/client/entity/EntityPlayerSP";
                                            fieldInsnNode.name = "posY";
                                            numReplacements++;

                                            // We went back two with .previous(), so go forwards again the same amount
                                            iterator.next();
                                            iterator.next();
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                if (numReplacements != 0) {
                    log("Replaced \"axisalignedbb.minY\" with \"this.posY\" " + numReplacements + " time" + (numReplacements == 1 ? "" : "s") + " in " + classNode.name);
                    onUpdateWalkingPlayerModified = true;
                } else {
                    throw new RuntimeException("[UpAndDownAndAllAround] Failed to find any instances of \"axisalignedbb.minY\" in " + classNode.name);
                }
            }
            /*


             */
            else if (methodNode.name.equals("onLivingUpdate")) {
                log("Modifying EntityPlayerSP::onLivingUpdate");

                boolean error = true;
                for (ListIterator<AbstractInsnNode> iterator = methodNode.instructions.iterator(); iterator.hasNext(); ) {
                    AbstractInsnNode next = iterator.next();
                    if (next instanceof VarInsnNode) {
                        VarInsnNode varInsnNode = (VarInsnNode) next;
                        if (varInsnNode.var == 6 && varInsnNode.getOpcode() == Opcodes.ASTORE) {
                            log("Found \"ASTORE 6\"");

                            VarInsnNode aLoad6 = new VarInsnNode(Opcodes.ALOAD, 6);
                            TypeInsnNode instanceofGravityAxisAlignedBB = new TypeInsnNode(Opcodes.INSTANCEOF, "uk/co/mysterymayhem/gravitymod/util/GravityAxisAlignedBB");
                            JumpInsnNode ifeqJumpInsnNode = new JumpInsnNode(Opcodes.IFEQ, null);
                            VarInsnNode aLoad0 = new VarInsnNode(Opcodes.ALOAD, 0);
                            VarInsnNode aLoad6_2 = new VarInsnNode(Opcodes.ALOAD, 6);
                            MethodInsnNode callHook = new MethodInsnNode(Opcodes.INVOKESTATIC,
                                    "uk/co/mysterymayhem/gravitymod/asm/Hooks",
                                    "pushEntityPlayerSPOutOfBlocks",
                                    "(Luk/co/mysterymayhem/gravitymod/asm/EntityPlayerWithGravity;Lnet/minecraft/util/math/AxisAlignedBB;)V", false);
                            JumpInsnNode gotoJumpInsnNode = new JumpInsnNode(Opcodes.GOTO, null);

                            iterator.add(aLoad6);
                            iterator.add(instanceofGravityAxisAlignedBB);
                            iterator.add(ifeqJumpInsnNode);
                            iterator.add(aLoad0);
                            iterator.add(aLoad6_2);
                            iterator.add(callHook);
                            iterator.add(gotoJumpInsnNode);

                            next = iterator.next();

                            if (next instanceof LabelNode) {
                                ifeqJumpInsnNode.label = (LabelNode) next;

                                next = iterator.next();

                                if (next instanceof LineNumberNode) {
                                    // ClassWriter is going to compute the frames, the commented out code is untested
//                                    FrameNode frameAppendNode = new FrameNode(Opcodes.F_APPEND, 1, new Object[]{"net/minecraft/util/math/AxisAlignedBB"}, 0, new Object[0]);
//                                    iterator.add(frameAppendNode);

                                    LabelNode labelForGotoJumpInsnNode = null;
                                    for (; iterator.hasNext(); ) {
                                        next = iterator.next();
                                        if (next instanceof MethodInsnNode) {
                                            MethodInsnNode methodInsnNode = (MethodInsnNode) next;
                                            if (methodInsnNode.getOpcode() == Opcodes.INVOKEVIRTUAL
                                                    && methodInsnNode.owner.equals("net/minecraft/client/entity/EntityPlayerSP")
                                                    && methodInsnNode.name.equals("getFoodStats")
                                                    && methodInsnNode.desc.equals("()Lnet/minecraft/util/FoodStats;")) {
                                                log("Found \"this.getFoodStats\"");
                                                for (; iterator.hasPrevious(); ) {
                                                    AbstractInsnNode previous = iterator.previous();
                                                    if (previous instanceof LabelNode) {
                                                        log("Found previous Label");
                                                        labelForGotoJumpInsnNode = (LabelNode) previous;
                                                        // ClassWriter is going to compute the frames, the commented out code is untested
//                                                        for(;iterator.hasNext();) {
//                                                            next = iterator.next();
//                                                            if (next instanceof VarInsnNode) {
//                                                                varInsnNode = (VarInsnNode)next;
//                                                                if (varInsnNode.getOpcode() == Opcodes.ALOAD && varInsnNode.var == 0) {
//                                                                    FrameNode frameSameNode = new FrameNode(Opcodes.F_SAME, 0, new Object[0], 0, new Object[0]);
//                                                                    break;
//                                                                }
//                                                            }
//                                                        }
                                                        break;
                                                    }
                                                }
                                                break;
                                            }
                                        }
                                    }
                                    if (labelForGotoJumpInsnNode != null) {
                                        gotoJumpInsnNode.label = labelForGotoJumpInsnNode;
                                        error = false;
                                    }
                                    break;
                                }
                            }
                            break;
                        }
                    }
                }
                if (error) {
                    throw new RuntimeException("[UpAndDownAndAllAround] Hook insertion into EntityPlayerSP::onLivingUpdate failed");
                } else {
                    log("Inserted an if statement and call to \"Hooks.pushEntityPlayerSPOutOfBlocks\" into \"EntityPlayerSP::onLivingUpdate\"");
                    onLivingUpdateModified = true;
                }
            }
            /*


             */
            else if (methodNode.name.equals("isHeadspaceFree")) {
                log("Modifying EntityPlayerSP::isHeadspaceFree");
                InsnList instructions = methodNode.instructions;
                AbstractInsnNode label = instructions.get(0);
                AbstractInsnNode lineNumber = instructions.get(1);
                AbstractInsnNode labelEnd = instructions.getLast();
                AbstractInsnNode returnNode = labelEnd.getPrevious();

                instructions.clear();
                instructions.add(label);
                instructions.add(lineNumber);
                instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
                instructions.add(new VarInsnNode(Opcodes.ALOAD, 1));
                instructions.add(new VarInsnNode(Opcodes.ILOAD, 2));
                instructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC,
                        "uk/co/mysterymayhem/gravitymod/asm/Hooks",
                        "isHeadspaceFree",
                        "(Lnet/minecraft/client/entity/EntityPlayerSP;Lnet/minecraft/util/math/BlockPos;I)Z",
                        false));
                instructions.add(new InsnNode(Opcodes.IRETURN));
                instructions.add(labelEnd);
                log("Replaced EntityPlayerSP::isHeadspaceFree with call to Hooks::isHeadspaceFree");
                isHeadSpaceFreeModified = true;
            }
            /*


             */
            else if (methodNode.name.equals("func_189810_i")) {
                int addVectorReplacements = 0;
                int blockPosUPCount = 0;
                int blockPosUPCountI = 0;
                boolean foundFirstBlockPosGetY = false;
                //newPATCH #1
                boolean patch1Complete = false;
                //newPATCH #2
                boolean patch2Complete = false;
                //newPATCH #3
                boolean patch3Complete = false;
                //newPATCH #4
                boolean patch4Complete = false;
                //newPATCH #4.5
                boolean patch4Point5Complete = false;
                int patch4Point5ReplacementCount = 0;
                //newPATCH #5 and #6.5
                boolean patch5Complete = false;
                int vec3d12_var = -1;
                boolean patch5Point5Complete = false;
                boolean patch6Complete = false;
                boolean patch7Complete = false;
                boolean patch8Complete = false;
                boolean patch9Complete = false;
                int axisAlignedBBmaxYCount = 0;
                int axisAlignedBBminYCount = 0;

                if (DEBUG_AUTO_JUMP) {
                    for (ListIterator<AbstractInsnNode> iterator = methodNode.instructions.iterator(); iterator.hasNext(); ) {
                        AbstractInsnNode next = iterator.next();
                        if (next instanceof LineNumberNode) {
                            iterator.add(new VarInsnNode(Opcodes.ALOAD, 0));
                            iterator.add(new VarInsnNode(Opcodes.FLOAD, 1));
                            iterator.add(new VarInsnNode(Opcodes.FLOAD, 2));
                            iterator.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "net/minecraft/client/entity/AbstractClientPlayer", "func_189810_i", "(FF)V", false));
                            iterator.add(new InsnNode(Opcodes.RETURN));
                        }
                    }
                }
                else {
                    for (ListIterator<AbstractInsnNode> iterator = methodNode.instructions.iterator(); iterator.hasNext(); ) {
                        AbstractInsnNode next = iterator.next();
                        if (next instanceof MethodInsnNode) {
                            MethodInsnNode methodInsnNode = (MethodInsnNode)next;
                            //newPATCH #4
                            if (patch3Complete
                                    && !patch4Complete
                                    && methodInsnNode.getOpcode() == Opcodes.INVOKEVIRTUAL
                                    && methodInsnNode.owner.equals("net/minecraft/client/entity/EntityPlayerSP")
                                    && methodInsnNode.name.equals("getEntityBoundingBox")
                                    && methodInsnNode.desc.equals("()Lnet/minecraft/util/math/AxisAlignedBB;")) {
                                iterator.remove();
                                iterator.next();
                                iterator.remove(); // GETFIELD net/minecraft/util/math/AxisAlignedBB.minY : D
                                iterator.add(new MethodInsnNode(Opcodes.INVOKESTATIC,
                                        "uk/co/mysterymayhem/gravitymod/asm/Hooks",
                                        "getOriginRelativePosY",
                                        "(Lnet/minecraft/entity/Entity;)D",
                                        false));
                                iterator.next(); // DLOAD ? (likely 7)
                                iterator.next(); // INVOKESPECIAL net/minecraft/util/math/Vec3d.<init> (DDD)V
                                iterator.add(new VarInsnNode(Opcodes.ALOAD, 0)); // Have to pass this to adjustVec as well
                                iterator.add(new MethodInsnNode(Opcodes.INVOKESTATIC,
                                        "uk/co/mysterymayhem/gravitymod/asm/Hooks",
                                        "adjustVec",
                                        "(Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/entity/Entity;)Lnet/minecraft/util/math/Vec3d;",
                                        false));
                                patch4Complete = true;
                            }
                            //newPatch #5
                            else if (patch4Point5Complete
                                    && !patch5Complete
                                    && methodInsnNode.getOpcode() == Opcodes.INVOKEVIRTUAL
                                    && methodInsnNode.owner.equals("net/minecraft/util/math/Vec3d")
                                    && methodInsnNode.name.equals("scale")
                                    && methodInsnNode.desc.equals("(D)Lnet/minecraft/util/math/Vec3d;")) {
                                next = iterator.next();
                                if (next instanceof VarInsnNode) {
                                    // Should also be ASTORE
                                    vec3d12_var = ((VarInsnNode)next).var;
                                    patch5Complete = true;
                                }
                                else {
                                    throw new RuntimeException("Expected a VarInsnNode after first usage of Vec3d::scale");
                                }
                            }
                            else if (patch5Complete
                                    && !patch5Point5Complete
                                    && methodInsnNode.getOpcode() == Opcodes.INVOKEVIRTUAL
                                    && methodInsnNode.owner.equals("net/minecraft/client/entity/EntityPlayerSP")
                                    && methodInsnNode.name.equals("func_189651_aD")
                                    && methodInsnNode.desc.equals("()Lnet/minecraft/util/math/Vec3d;")) {
                                methodInsnNode.setOpcode(Opcodes.INVOKESTATIC);
                                methodInsnNode.owner = "uk/co/mysterymayhem/gravitymod/asm/Hooks";
                                methodInsnNode.name = "getRelativeLookVec";
                                methodInsnNode.desc = "(Lnet/minecraft/entity/Entity;)Lnet/minecraft/util/math/Vec3d;";
                                patch5Point5Complete = true;
                            }
                            //PATCH #1
                            else if (methodInsnNode.getOpcode() == Opcodes.INVOKEVIRTUAL
                                    && methodInsnNode.owner.equals("net/minecraft/util/math/Vec3d")
                                    && methodInsnNode.name.equals("addVector")
                                    && methodInsnNode.desc.equals("(DDD)Lnet/minecraft/util/math/Vec3d;")){
                                iterator.remove();
                                iterator.add(new VarInsnNode(Opcodes.ALOAD, 0));
                                iterator.add(new MethodInsnNode(Opcodes.INVOKESTATIC,
                                        "uk/co/mysterymayhem/gravitymod/asm/Hooks",
                                        "addAdjustedVector",
                                        "(Lnet/minecraft/util/math/Vec3d;DDDLnet/minecraft/entity/Entity;)Lnet/minecraft/util/math/Vec3d;",
                                        false));
                                addVectorReplacements++;
                            }
                            //newPATCH #8
                            else if (patch7Complete
                                    && !patch8Complete
                                    && methodInsnNode.getOpcode() == Opcodes.INVOKESPECIAL
                                    && methodInsnNode.owner.equals("net/minecraft/util/math/AxisAlignedBB")
                                    && methodInsnNode.name.equals("<init>")
                                    && methodInsnNode.desc.equals("(Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/util/math/Vec3d;)V")) {
                                iterator.remove();
                                iterator.add(new VarInsnNode(Opcodes.ALOAD, 0));
                                iterator.add(new MethodInsnNode(Opcodes.INVOKESTATIC,
                                        "uk/co/mysterymayhem/gravitymod/asm/Hooks",
                                        "constructNewGAABBFrom2Vec3d",
                                        "(Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/entity/Entity;)Lnet/minecraft/util/math/AxisAlignedBB;",
                                        false));
                                patch8Complete = true;
                            }
                            //PATCH #3
                            else if (methodInsnNode.getOpcode() == Opcodes.INVOKEVIRTUAL
                                    && methodInsnNode.owner.equals("net/minecraft/util/math/BlockPos")
                                    && methodInsnNode.name.equals("up")) {
                                String desc = null;
                                if (methodInsnNode.desc.equals("()Lnet/minecraft/util/math/BlockPos;")) {
                                    desc = "(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/entity/Entity;)Lnet/minecraft/util/math/BlockPos;";
                                    blockPosUPCount++;
                                }
                                else if (methodInsnNode.desc.equals("(I)Lnet/minecraft/util/math/BlockPos;")) {
                                    desc = "(Lnet/minecraft/util/math/BlockPos;ILnet/minecraft/entity/Entity;)Lnet/minecraft/util/math/BlockPos;";
                                    blockPosUPCountI++;
                                }

                                //Just in case there's ever some other 'up' method
                                if (desc != null) {
                                    iterator.remove();
                                    iterator.add(new VarInsnNode(Opcodes.ALOAD, 0));
                                    iterator.add(new MethodInsnNode(Opcodes.INVOKESTATIC,
                                            "uk/co/mysterymayhem/gravitymod/asm/Hooks",
                                            "getRelativeUpBlockPos",
                                            desc,
                                            false));
                                }
                            }
                            //PATCH #4
                            else if (!foundFirstBlockPosGetY
                                    && methodInsnNode.getOpcode() == Opcodes.INVOKEVIRTUAL
                                    && methodInsnNode.owner.equals("net/minecraft/util/math/BlockPos")
                                    && methodInsnNode.name.equals("getY")
                                    && methodInsnNode.desc.equals("()I")) {
                                iterator.remove();
                                iterator.add(new VarInsnNode(Opcodes.ALOAD, 0));
                                iterator.add(new MethodInsnNode(Opcodes.INVOKESTATIC,
                                        "uk/co/mysterymayhem/gravitymod/asm/Hooks",
                                        "getRelativeYOfBlockPos",
                                        "(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/entity/Entity;)I",
                                        false));
                                foundFirstBlockPosGetY = true;
                            }

                        }
                        else if (next instanceof TypeInsnNode
                                && next.getOpcode() == Opcodes.NEW) {
                            TypeInsnNode typeInsnNode = (TypeInsnNode)next;
                            //newPATCH #1
                            if (!patch1Complete
//                                    && typeInsnNode.getOpcode() == Opcodes.NEW
                                    && typeInsnNode.desc.equals("net/minecraft/util/math/Vec3d")) {
                                iterator.remove();
                                while(!patch1Complete) {
                                    next = iterator.next();
                                    if (next instanceof MethodInsnNode && next.getOpcode() == Opcodes.INVOKESPECIAL) {
                                        iterator.remove();
                                        iterator.add(new VarInsnNode(Opcodes.ALOAD, 0));
                                        iterator.add(new MethodInsnNode(Opcodes.INVOKESTATIC,
                                                "uk/co/mysterymayhem/gravitymod/asm/Hooks",
                                                "getBottomOfEntity",
                                                "(Lnet/minecraft/entity/Entity;)Lnet/minecraft/util/math/Vec3d;",
                                                false));
                                        patch1Complete = true;
                                    }
                                    else {
                                        iterator.remove();
                                    }
                                }
                            }
                            //newPATCH #7
                            else if (!patch7Complete
                                    && patch6Complete
//                                    && typeInsnNode.getOpcode() == Opcodes.NEW
                                    && typeInsnNode.desc.equals("net/minecraft/util/math/AxisAlignedBB")) {
                                iterator.remove();
                                next = iterator.next();
                                if (next instanceof InsnNode && next.getOpcode() == Opcodes.DUP) {
                                    iterator.remove();
                                    patch7Complete = true;
                                }
                                else {
                                    //what
                                    throw new RuntimeException("[UpAndDownAndAllAround] DUP instruction not found after expected NEW AxisAlignedBB");
                                }
                            }
                            //newPATCH #6
                            else if(patch5Point5Complete
                                    && !patch6Complete
//                                    && typeInsnNode.getOpcode() == Opcodes.NEW
                                    && typeInsnNode.desc.equals("net/minecraft/util/math/BlockPos")) {
                                iterator.remove();
                                while(!patch6Complete) {
                                    next = iterator.next();
                                    if (next instanceof MethodInsnNode && next.getOpcode() == Opcodes.INVOKESPECIAL) {
                                        iterator.remove();
                                        iterator.add(new VarInsnNode(Opcodes.ALOAD, 0));
                                        iterator.add(new MethodInsnNode(Opcodes.INVOKESTATIC,
                                                "uk/co/mysterymayhem/gravitymod/asm/Hooks",
                                                "getBlockPosAtTopOfPlayer",
                                                "(Lnet/minecraft/entity/Entity;)Lnet/minecraft/util/math/BlockPos;",
                                                false));

                                        //newPATCH #6.5
                                        iterator.next(); //ASTORE ? (probably 17)
                                        iterator.add(new VarInsnNode(Opcodes.ALOAD, vec3d12_var));
                                        iterator.add(new VarInsnNode(Opcodes.ALOAD, 0));
                                        iterator.add(new MethodInsnNode(Opcodes.INVOKESTATIC,
                                                "uk/co/mysterymayhem/gravitymod/asm/Hooks",
                                                "adjustVec",
                                                "(Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/entity/Entity;)Lnet/minecraft/util/math/Vec3d;",
                                                false));
                                        iterator.add(new VarInsnNode(Opcodes.ASTORE, vec3d12_var));

                                        patch6Complete = true;
                                    }
                                    else {
                                        iterator.remove();
                                    }
                                }
                            }
                            //newPATCH #9
                            else if(patch8Complete
                                    && !patch9Complete
//                                    && typeInsnNode.getOpcode() == Opcodes.NEW
                                    && typeInsnNode.desc.equals("net/minecraft/util/math/Vec3d")) {
                                iterator.next(); // DUP
                                iterator.next(); // DCONST_0
                                next = iterator.next();
                                if (next.getOpcode() == Opcodes.DCONST_1) {
                                    iterator.remove();
                                    iterator.add(new InsnNode(Opcodes.DCONST_0));
                                }
                                else {
                                    throw new RuntimeException("Expecting DCONST_0 followed by DCONST_1, but instead got " + next);
                                }
                                iterator.next(); // DCONST_0
                                iterator.next(); // INVOKESPECIAL net/minecraft/util/math/Vec3d.<init> (DDD)V

                                iterator.add(new InsnNode(Opcodes.DCONST_0));
                                iterator.add(new InsnNode(Opcodes.DCONST_1));
                                iterator.add(new InsnNode(Opcodes.DCONST_0));
                                iterator.add(new VarInsnNode(Opcodes.ALOAD, 0));
                                iterator.add(new MethodInsnNode(Opcodes.INVOKESTATIC,
                                        "uk/co/mysterymayhem/gravitymod/asm/Hooks",
                                        "addAdjustedVector",
                                        "(Lnet/minecraft/util/math/Vec3d;DDDLnet/minecraft/entity/Entity;)Lnet/minecraft/util/math/Vec3d;",
                                        false));

                                patch9Complete = true;
                            }

                        }
                        else if (next instanceof FieldInsnNode) {
                            FieldInsnNode fieldInsnNode = (FieldInsnNode)next;
                            //newPATCH #2
                            if (patch1Complete
                                    && !patch2Complete
                                    && fieldInsnNode.getOpcode() == Opcodes.GETFIELD
                                    && fieldInsnNode.owner.equals("net/minecraft/client/entity/EntityPlayerSP")
                                    && fieldInsnNode.name.equals("posX")
                                    && fieldInsnNode.desc.equals("D")) {
                                iterator.remove();
                                iterator.add(new MethodInsnNode(Opcodes.INVOKESTATIC,
                                        "uk/co/mysterymayhem/gravitymod/asm/Hooks",
                                        "getOriginRelativePosX",
                                        "(Lnet/minecraft/entity/Entity;)D",
                                        false));
                                patch2Complete = true;
                            }
                            //newPATCH #3
                            else if(patch2Complete
                                    && !patch3Complete
                                    && fieldInsnNode.getOpcode() == Opcodes.GETFIELD
                                    && fieldInsnNode.owner.equals("net/minecraft/client/entity/EntityPlayerSP")
                                    && fieldInsnNode.name.equals("posZ")
                                    && fieldInsnNode.desc.equals("D")) {
                                iterator.remove();
                                iterator.add(new MethodInsnNode(Opcodes.INVOKESTATIC,
                                        "uk/co/mysterymayhem/gravitymod/asm/Hooks",
                                        "getOriginRelativePosZ",
                                        "(Lnet/minecraft/entity/Entity;)D",
                                        false));
                                patch3Complete = true;
                            }
                            else if(patch4Complete
                                    && !patch4Point5Complete
                                    && fieldInsnNode.getOpcode() == Opcodes.GETFIELD
                                    && fieldInsnNode.owner.equals("net/minecraft/client/entity/EntityPlayerSP")
                                    && fieldInsnNode.name.equals("rotationYaw")
                                    && fieldInsnNode.desc.equals("F")) {
                                iterator.remove();
                                iterator.add(new MethodInsnNode(Opcodes.INVOKESTATIC,
                                        "uk/co/mysterymayhem/gravitymod/asm/Hooks",
                                        "getRelativeYaw",
                                        "(Lnet/minecraft/entity/Entity;)F",
                                        false));
                                patch4Point5ReplacementCount++;
                                if (patch4Point5ReplacementCount >= 2) {
                                    patch4Point5Complete = true;
                                }
                            }
                            //newPATCH #10
                            else if (patch9Complete
                                    && axisAlignedBBmaxYCount < 2
                                    && fieldInsnNode.getOpcode() == Opcodes.GETFIELD
                                    && fieldInsnNode.owner.equals("net/minecraft/util/math/AxisAlignedBB")
                                    && fieldInsnNode.name.equals("maxY")
                                    && fieldInsnNode.desc.equals("D")) {
                                iterator.remove();
                                iterator.add(new VarInsnNode(Opcodes.ALOAD, 0));
                                iterator.add(new MethodInsnNode(Opcodes.INVOKESTATIC,
                                        "uk/co/mysterymayhem/gravitymod/asm/Hooks",
                                        "getRelativeTopOfBB",
                                        "(Lnet/minecraft/util/math/AxisAlignedBB;Lnet/minecraft/entity/Entity;)D",
                                        false));
                                axisAlignedBBmaxYCount++;
                            }
                            //newPATCH #11
                            else if (axisAlignedBBmaxYCount == 2
                                    && axisAlignedBBminYCount < 2
                                    && fieldInsnNode.getOpcode() == Opcodes.GETFIELD
                                    && fieldInsnNode.owner.equals("net/minecraft/util/math/AxisAlignedBB")
                                    && fieldInsnNode.name.equals("minY")
                                    && fieldInsnNode.desc.equals("D")) {
                                iterator.remove();
                                iterator.add(new VarInsnNode(Opcodes.ALOAD, 0));
                                iterator.add(new MethodInsnNode(Opcodes.INVOKESTATIC,
                                        "uk/co/mysterymayhem/gravitymod/asm/Hooks",
                                        "getRelativeBottomOfBB",
                                        "(Lnet/minecraft/util/math/AxisAlignedBB;Lnet/minecraft/entity/Entity;)D",
                                        false));
                                axisAlignedBBminYCount++;
                            }
                        }
                    }
//                    addVectorReplacements
//                    axisAlignedBBmaxYCount
//                    axisAlignedBBminYCount
//                    blockPosUPCount
//                    blockPosUPCountI
                    log("addVector: " + addVectorReplacements
                            + ", maxY: " + axisAlignedBBmaxYCount
                            + ", minY: " + axisAlignedBBminYCount
                            + ", up(): " + blockPosUPCount
                            + ", up(int): " + blockPosUPCountI);
                }

                if (!patch9Complete && !DEBUG_AUTO_JUMP) {
                    throw new RuntimeException("[UpAndDown] Error occured while patching EntityPlayerSP");
                }
            }
            else if (methodNode.name.equals("moveEntity")) {
                if (DEBUG_AUTO_JUMP) {
                    for (ListIterator<AbstractInsnNode> iterator = methodNode.instructions.iterator(); iterator.hasNext(); ) {
                        AbstractInsnNode next = iterator.next();
                        if (next instanceof LineNumberNode) {
                            iterator.add(new VarInsnNode(Opcodes.ALOAD, 0));
                            iterator.add(new VarInsnNode(Opcodes.DLOAD, 1));
                            iterator.add(new VarInsnNode(Opcodes.DLOAD, 3));
                            iterator.add(new VarInsnNode(Opcodes.DLOAD, 5));
                            iterator.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "net/minecraft/client/entity/AbstractClientPlayer",
                                    "moveEntity", "(DDD)V", false));
                            iterator.add(new InsnNode(Opcodes.RETURN));
                        }
                    }
                }
                else {
                    int prevRelativeXPos_var = -1;
                    int prevRelativeZPos_var = -1;

                    outerfor:
                    for (ListIterator<AbstractInsnNode> iterator = methodNode.instructions.iterator(); iterator.hasNext(); ) {
                        AbstractInsnNode next = iterator.next();
                        if (next instanceof VarInsnNode && next.getOpcode() == Opcodes.ALOAD) {
                            iterator.add(new VarInsnNode(Opcodes.ALOAD, 0));
                            iterator.next(); // GETFIELD net/minecraft/client/entity/EntityPlayerSP.posX : D
                            iterator.add(new VarInsnNode(Opcodes.ALOAD, 0));
                            iterator.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/entity/EntityPlayerSP", "posY", "D"));
                            iterator.add(new VarInsnNode(Opcodes.ALOAD, 0));
                            iterator.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/entity/EntityPlayerSP", "posZ", "D"));
                            iterator.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "uk/co/mysterymayhem/gravitymod/asm/Hooks",
                                    "inverseAdjustXYZ", "(Lnet/minecraft/entity/Entity;DDD)[D", false));
                            iterator.add(new InsnNode(Opcodes.DUP));
                            iterator.add(new InsnNode(Opcodes.ICONST_0));
                            iterator.add(new InsnNode(Opcodes.DALOAD));
                            //next is DSTORE 7
                            next = iterator.next();
                            if (next instanceof VarInsnNode) {
                                prevRelativeXPos_var = ((VarInsnNode)next).var;
                            }
                            else {
                                throw new RuntimeException("Was expecting DSTORE _ after GETFIELD posX, instead got " + next);
                            }

                            while(true) {
                                next = iterator.next();
                                if (next instanceof VarInsnNode && next.getOpcode() == Opcodes.ALOAD) {
                                    //ALOAD 0
                                    iterator.remove();
                                    iterator.next(); //GETFIELD net/minecraft/client/entity/EntityPlayerSP.posZ : D
                                    iterator.remove();
                                    iterator.add(new InsnNode(Opcodes.ICONST_2));
                                    iterator.add(new InsnNode(Opcodes.DALOAD));
                                    //next is DSTORE 9
                                    next = iterator.next();
                                    if (next instanceof VarInsnNode) {
                                        prevRelativeZPos_var = ((VarInsnNode)next).var;
                                    }
                                    else {
                                        throw new RuntimeException("Was expecting DSTORE _ after GETFIELD posZ, instead got " + next);
                                    }

                                    while(true) {
                                        next = iterator.next();
                                        if(next instanceof FieldInsnNode) {
                                            FieldInsnNode fieldInsnNode = (FieldInsnNode)next;
                                            if (fieldInsnNode.getOpcode() == Opcodes.GETFIELD
                                                    && fieldInsnNode.owner.equals("net/minecraft/client/entity/EntityPlayerSP")
                                                    && fieldInsnNode.name.equals("posX")
                                                    && fieldInsnNode.desc.equals("D")) {
                                                iterator.previous();
                                                iterator.add(new VarInsnNode(Opcodes.ALOAD, 0));
                                                iterator.next(); // same GETFIELD instruction
                                                iterator.add(new VarInsnNode(Opcodes.ALOAD, 0));
                                                iterator.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/entity/EntityPlayerSP", "posY", "D"));
                                                iterator.add(new VarInsnNode(Opcodes.ALOAD, 0));
                                                iterator.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/entity/EntityPlayerSP", "posZ", "D"));
                                                iterator.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "uk/co/mysterymayhem/gravitymod/asm/Hooks",
                                                        "inverseAdjustXYZ", "(Lnet/minecraft/entity/Entity;DDD)[D", false));
                                                iterator.add(new InsnNode(Opcodes.DUP));
                                                iterator.add(new InsnNode(Opcodes.ICONST_2));
                                                iterator.add(new InsnNode(Opcodes.DALOAD));
                                                iterator.add(new VarInsnNode(Opcodes.DLOAD, prevRelativeZPos_var));
                                                iterator.add(new InsnNode(Opcodes.DSUB));
                                                iterator.add(new VarInsnNode(Opcodes.DSTORE, prevRelativeZPos_var));
                                                iterator.add(new InsnNode(Opcodes.ICONST_0));
                                                iterator.add(new InsnNode(Opcodes.DALOAD));
                                                iterator.add(new VarInsnNode(Opcodes.DLOAD, prevRelativeXPos_var));
                                                iterator.add(new InsnNode(Opcodes.DSUB));
                                                iterator.add(new InsnNode(Opcodes.D2F));
                                                iterator.add(new VarInsnNode(Opcodes.DLOAD, prevRelativeZPos_var));
                                                iterator.add(new InsnNode(Opcodes.D2F));

                                                while(true) {
                                                    next = iterator.next();
                                                    if (next.getOpcode() == Opcodes.INVOKEVIRTUAL) {
                                                        //INVOKEVIRTUAL net/minecraft/client/entity/EntityPlayerSP.func_189810_i (FF)V // Auto-jump method
                                                        break outerfor;
                                                    }
                                                    else {
                                                        iterator.remove();
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    /*
                    // access flags 0x1
                      public moveEntity(DDD)V
                       L0
                        LINENUMBER 602 L0
                        ALOAD 0
                        ALOAD 0
                        GETFIELD uk/co/mysterymayhem/gravitymod/asm/EntityPlayerWithGravity.posX : D
                        ALOAD 0
                        GETFIELD uk/co/mysterymayhem/gravitymod/asm/EntityPlayerWithGravity.posY : D
                        ALOAD 0
                        GETFIELD uk/co/mysterymayhem/gravitymod/asm/EntityPlayerWithGravity.posZ : D
                        INVOKESTATIC uk/co/mysterymayhem/gravitymod/asm/Hooks.inverseAdjustXYZ (Lnet/minecraft/entity/Entity;DDD)[D
                        DUP
                        ICONST_0
                        DALOAD
                        DSTORE 7
                       L1
                        LINENUMBER 604 L2
                        ICONST_2
                        DALOAD
                        DSTORE 9
                       L2
                        LINENUMBER 605 L3
                        ALOAD 0
                        DLOAD 1
                        DLOAD 3
                        DLOAD 5
                        INVOKESPECIAL net/minecraft/entity/player/EntityPlayer.moveEntity (DDD)V
                       L3
                        LINENUMBER 606 L4
                        ALOAD 0
                          this
                        ALOAD 0
                          this, this
                        ALOAD 0
                          this, this, this
                        GETFIELD uk/co/mysterymayhem/gravitymod/asm/EntityPlayerWithGravity.posX : D
                          this, this, posX
                        ALOAD 0
                          this, this, posX, this
                        GETFIELD uk/co/mysterymayhem/gravitymod/asm/EntityPlayerWithGravity.posY : D
                          this, this, posX, posY
                        ALOAD 0
                          this, this, posX, posY, this
                        GETFIELD uk/co/mysterymayhem/gravitymod/asm/EntityPlayerWithGravity.posZ : D
                          this, this, posX, posY, posZ
                        INVOKESTATIC uk/co/mysterymayhem/gravitymod/asm/Hooks.inverseAdjustXYZ (Lnet/minecraft/entity/Entity;DDD)[D
                          this, doubles
                        DUP
                          this, doubles, doubles
                        ICONST_2
                          this, doubles, doubles, 2
                        DALOAD
                          this, doubles, doubles[2](newRelZ)
                        DLOAD 9
                          this, doubles, doubles[2](newRelZ), d1(oldRelZ)
                        DSUB
                          this, doubles, doubles[2](newRelZ)-d1(oldRelZ)
                        DSTORE 9
                          this, doubles
                        ICONST_0
                          this, doubles, 0
                        DALOAD
                          this, doubles[0](newRelX)
                        DLOAD 7
                          this, doubles[0](newRelX), d0(oldRelX)
                        DSUB
                          this, doubles[0](newRelX)-d0(oldRelX)
                        D2F
                          this, (float)doubles[0](newRelX)-d0(oldRelX)
                        DLOAD 9
                          this, (float)doubles[0](newRelX)-d0(oldRelX), doubles[2](newRelZ)-d1(oldRelZ)
                        D2F
                          this, (float)doubles[0](newRelX)-d0(oldRelX), (float)doubles[2](newRelZ)-d1(oldRelZ)
                        INVOKEVIRTUAL uk/co/mysterymayhem/gravitymod/asm/EntityPlayerWithGravity.func_189810_i (FF)V
                          <empty>
                       L4
                        LINENUMBER 608 L6
                        RETURN
                       L5
                     */
                }
            }
        }

        if (isHeadSpaceFreeModified && onLivingUpdateModified && onUpdateWalkingPlayerModified) {
//            ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
            ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);
            classNode.accept(classWriter);

//            byte[] modifiedBytes = classWriter.toByteArray();
//
//            ClassNode classNode2 = new ClassNode();
//            ClassReader classReader2 = new ClassReader(modifiedBytes);
//            PrintWriter writer = new PrintWriter(System.out);
//            TraceClassVisitor traceClassVisitor = new TraceClassVisitor(classNode2, writer);
//            classReader2.accept(traceClassVisitor, 0);
//            return modifiedBytes;

            return classWriter.toByteArray();
        }
        else {
            throw new RuntimeException("[UpAndDownAndAllAround] Modification of EntityPlayerSP failed, UpAndDownAndAllAround is probably being used with the wrong Minecraft version.");
        }

    }

    private static final int GET_ROTATIONYAW = 0b1;
    private static final int GET_ROTATIONPITCH = 0b10;
    private static final int GET_PREVROTATIONYAW = 0b100;
    private static final int GET_PREVROTATIONPITCH = 0b1000;
    private static final int GET_ROTATIONYAWHEAD = 0b10000;
    private static final int GET_PREVROTATIONYAWHEAD = 0b100000;
    private static final int ALL_GET_ROTATION_VARS = GET_ROTATIONYAW | GET_ROTATIONPITCH | GET_PREVROTATIONYAW
                                            | GET_PREVROTATIONPITCH | GET_ROTATIONYAWHEAD | GET_PREVROTATIONYAWHEAD;

    private static void patchMethodUsingRelativeRotations(MethodNode methodNode, int fieldBits) {
        if (fieldBits == 0) {
            return;
        }

        boolean changeRotationYaw = (fieldBits & GET_ROTATIONYAW) == GET_ROTATIONYAW;
        boolean changeRotationPitch = (fieldBits & GET_ROTATIONPITCH) == GET_ROTATIONPITCH;
        boolean changePrevRotationYaw = (fieldBits & GET_PREVROTATIONYAW) == GET_PREVROTATIONYAW;
        boolean changePrevRotationPitch = (fieldBits & GET_PREVROTATIONPITCH) == GET_PREVROTATIONPITCH;

        for (ListIterator<AbstractInsnNode> iterator = methodNode.instructions.iterator(); iterator.hasNext(); ) {
            AbstractInsnNode next = iterator.next();
            if (next.getOpcode() == Opcodes.GETFIELD/* && next instanceof FieldInsnNode*/) {
                FieldInsnNode fieldInsnNode = (FieldInsnNode)next;
                // This will not pick up EntityOtherPlayerMP or EntityPlayerSP which are in net/minecraft/client/entity
                if ((fieldInsnNode.owner.startsWith("net/minecraft/entity/") || fieldInsnNode.owner.startsWith("net/minecraft/client/entity/"))
                        && fieldInsnNode.desc.equals("F")) {
                    if (changeRotationYaw && fieldInsnNode.name.equals("rotationYaw")) {
                        iterator.remove();
                        iterator.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "uk/co/mysterymayhem/gravitymod/asm/Hooks",
                                "getAdjustedYaw", "(Lnet/minecraft/entity/Entity;)F", false));
                    }
                    else if (changeRotationPitch && fieldInsnNode.name.equals("rotationPitch")) {
                        iterator.remove();
                        iterator.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "uk/co/mysterymayhem/gravitymod/asm/Hooks",
                                "getAdjustedPitch", "(Lnet/minecraft/entity/Entity;)F", false));
                    }
                    else if (changePrevRotationYaw && fieldInsnNode.name.equals("prevRotationYaw")) {
                        iterator.remove();
                        iterator.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "uk/co/mysterymayhem/gravitymod/asm/Hooks",
                                "getAdjustedPrevYaw", "(Lnet/minecraft/entity/Entity;)F", false));
                    }
                    else if (changePrevRotationPitch && fieldInsnNode.name.equals("prevRotationPitch")) {
                        iterator.remove();
                        iterator.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "uk/co/mysterymayhem/gravitymod/asm/Hooks",
                                "getAdjustedPrevPitch", "(Lnet/minecraft/entity/Entity;)F", false));
                    }
                }
            }
        }
    }

    private static void patchMethodUsingAbsoluteRotations(MethodNode methodNode, int fieldBits) {
        if (fieldBits == 0) {
            return;
        }

        boolean changeRotationYaw = (fieldBits & GET_ROTATIONYAW) == GET_ROTATIONYAW;
        boolean changeRotationPitch = (fieldBits & GET_ROTATIONPITCH) == GET_ROTATIONPITCH;
        boolean changePrevRotationYaw = (fieldBits & GET_PREVROTATIONYAW) == GET_PREVROTATIONYAW;
        boolean changePrevRotationPitch = (fieldBits & GET_PREVROTATIONPITCH) == GET_PREVROTATIONPITCH;

        // Field introduced in EntityLivingBase
        boolean changeRotationYawHead = (fieldBits & GET_ROTATIONYAWHEAD) == GET_ROTATIONYAWHEAD;
        boolean changePrevRotationYawHead = (fieldBits & GET_PREVROTATIONYAWHEAD) == GET_PREVROTATIONYAWHEAD;


        for (ListIterator<AbstractInsnNode> iterator = methodNode.instructions.iterator(); iterator.hasNext(); ) {
            AbstractInsnNode next = iterator.next();
            if (next.getOpcode() == Opcodes.GETFIELD/* && next instanceof FieldInsnNode*/) {
                FieldInsnNode fieldInsnNode = (FieldInsnNode)next;
                // This will not pick up EntityOtherPlayerMP or EntityPlayerSP which are in net/minecraft/client/entity
                if ((fieldInsnNode.owner.startsWith("net/minecraft/entity/") || fieldInsnNode.owner.startsWith("net/minecraft/client/entity/"))
                        && fieldInsnNode.desc.equals("F")) {
                    if (changeRotationYaw && fieldInsnNode.name.equals("rotationYaw")) {
                        iterator.remove();
                        iterator.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "uk/co/mysterymayhem/gravitymod/asm/Hooks",
                                "getRelativeYaw", "(Lnet/minecraft/entity/Entity;)F", false));
                    }
                    else if (changeRotationPitch && fieldInsnNode.name.equals("rotationPitch")) {
                        iterator.remove();
                        iterator.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "uk/co/mysterymayhem/gravitymod/asm/Hooks",
                                "getRelativePitch", "(Lnet/minecraft/entity/Entity;)F", false));
                    }
                    else if (changePrevRotationYaw && fieldInsnNode.name.equals("prevRotationYaw")) {
                        iterator.remove();
                        iterator.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "uk/co/mysterymayhem/gravitymod/asm/Hooks",
                                "getRelativePrevYaw", "(Lnet/minecraft/entity/Entity;)F", false));
                    }
                    else if (changePrevRotationPitch && fieldInsnNode.name.equals("prevRotationPitch")) {
                        iterator.remove();
                        iterator.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "uk/co/mysterymayhem/gravitymod/asm/Hooks",
                                "getRelativePrevPitch", "(Lnet/minecraft/entity/Entity;)F", false));
                    }
                    else if (changeRotationYawHead && fieldInsnNode.name.equals("rotationYawHead")) {
                        iterator.remove();
                        iterator.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "uk/co/mysterymayhem/gravitymod/asm/Hooks",
                                "getRelativeYawHead", "(Lnet/minecraft/entity/EntityLivingBase;)F", false));
                    }
                    else if (changePrevRotationYawHead && fieldInsnNode.name.equals("prevRotationYawHead")) {
                        iterator.remove();
                        iterator.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "uk/co/mysterymayhem/gravitymod/asm/Hooks",
                                "getPrevRelativeYawHead", "(Lnet/minecraft/entity/EntityLivingBase;)F", false));
                    }
                }
            }
        }
    }

    private static byte[] patchBlockChestAndBlockCocoa(byte[] bytes) {
        ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(bytes);
        classReader.accept(classNode, 0);

        for (MethodNode methodNode : classNode.methods) {
            if (methodNode.name.equals("onBlockPlacedBy")) {
                patchMethodUsingRelativeRotations(methodNode, GET_ROTATIONYAW);
                break;
            }
        }
        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        classNode.accept(classWriter);
        return classWriter.toByteArray();
    }

    private static byte[] patchBlockFenceGate(byte[] bytes) {
        ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(bytes);
        classReader.accept(classNode, 0);

        for (MethodNode methodNode : classNode.methods) {
            if (methodNode.name.equals("onBlockActivated")) {
                patchMethodUsingRelativeRotations(methodNode, GET_ROTATIONYAW);
                break;
            }
        }
        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        classNode.accept(classWriter);
        return classWriter.toByteArray();
    }

    private static byte[] patchParticleManager(byte[] bytes) {
        ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(bytes);
        classReader.accept(classNode, 0);

        for (MethodNode methodNode : classNode.methods) {
//            if (methodNode.name.equals("renderLitParticles")) {
//                patchMethodUsingRelativeRotations(methodNode, ROTATIONYAW + ROTATIONPITCH);
//                break;
//            }
            if (methodNode.name.equals("renderParticles")) {
                for (ListIterator<AbstractInsnNode> iterator = methodNode.instructions.iterator(); iterator.hasNext(); ) {
                    AbstractInsnNode next = iterator.next();
                    if (next instanceof MethodInsnNode) {
                        MethodInsnNode methodInsnNode = (MethodInsnNode)next;
                        if (methodInsnNode.getOpcode() == Opcodes.INVOKEVIRTUAL
                                && methodInsnNode.owner.equals("net/minecraft/entity/Entity")
                                && methodInsnNode.name.equals("getLook")
                                && methodInsnNode.desc.equals("(F)Lnet/minecraft/util/math/Vec3d;")) {
                            methodInsnNode.setOpcode(Opcodes.INVOKESTATIC);
                            methodInsnNode.owner = "uk/co/mysterymayhem/gravitymod/asm/Hooks";
                            methodInsnNode.name = "getNonGAffectedLook";
                            methodInsnNode.desc = "(Lnet/minecraft/entity/Entity;F)Lnet/minecraft/util/math/Vec3d;";
                        }
                    }
                }
            }
        }
        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        classNode.accept(classWriter);
        return classWriter.toByteArray();
    }

    private static byte[] patchActiveRenderInfo(byte[] bytes) {
        ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(bytes);
        classReader.accept(classNode, 0);

        for (MethodNode methodNode : classNode.methods) {
            if (methodNode.name.equals("updateRenderInfo")) {
                patchMethodUsingRelativeRotations(methodNode, GET_ROTATIONYAW + GET_ROTATIONPITCH);
                break;
            }
        }
        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        classNode.accept(classWriter);
        return classWriter.toByteArray();
    }

    private static byte[] patchParticle(byte[] bytes) {
        ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(bytes);
        classReader.accept(classNode, 0);

        outerfor:
        for (MethodNode methodNode : classNode.methods) {
            if (methodNode.name.equals("renderParticle")) {
                for (ListIterator<AbstractInsnNode> iterator = methodNode.instructions.iterator(); iterator.hasNext(); ) {
                    AbstractInsnNode next = iterator.next();
                    if(next instanceof VarInsnNode) {
                        VarInsnNode varInsnNode = (VarInsnNode)next;
                        if(varInsnNode.getOpcode() == Opcodes.ASTORE) {
                            iterator.previous();
                            iterator.add(new VarInsnNode(Opcodes.ALOAD, 2)); //passed entity argument
                            iterator.add(new MethodInsnNode(Opcodes.INVOKESTATIC,
                                    "uk/co/mysterymayhem/gravitymod/asm/Hooks",
                                    "adjustVecs",
                                    "([Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/entity/Entity;)[Lnet/minecraft/util/math/Vec3d;",
                                    false));
                            break outerfor;
                        }
                    }
                }
            }
        }
        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        classNode.accept(classWriter);
        return classWriter.toByteArray();
    }

    private static byte[] patchSoundManager(byte[] bytes) {
        ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(bytes);
        classReader.accept(classNode, 0);

        for (MethodNode methodNode : classNode.methods) {

            if (methodNode.name.equals("setListener")) {
                for (ListIterator<AbstractInsnNode> iterator = methodNode.instructions.iterator(); iterator.hasNext(); ) {
                    AbstractInsnNode next = iterator.next();
                    if (next instanceof  MethodInsnNode) {
                        MethodInsnNode methodInsnNode = (MethodInsnNode) next;
                        if (methodInsnNode.name.equals("setListenerOrientation")) {
                            methodInsnNode.owner = "uk/co/mysterymayhem/gravitymod/asm/Hooks";
                            methodInsnNode.name = "setListenerOrientationHook";
                            methodInsnNode.desc = "(Lpaulscode/sound/SoundSystem;FFFFFFLnet/minecraft/entity/player/EntityPlayer;)V";
                            methodInsnNode.setOpcode(Opcodes.INVOKESTATIC);
                            iterator.previous();
                            iterator.add(new VarInsnNode(Opcodes.ALOAD, 1)); // load EntityPlayer method argument
                        }
                    }
                }
                patchMethodUsingAbsoluteRotations(methodNode, GET_ROTATIONYAW + GET_PREVROTATIONYAW + GET_ROTATIONPITCH + GET_PREVROTATIONPITCH);
            }
        }
        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        classNode.accept(classWriter);
        return classWriter.toByteArray();
    }

    private static byte[] patchItemBow(byte[] bytes) {
        ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(bytes);
        classReader.accept(classNode, 0);

        for (MethodNode methodNode : classNode.methods) {
            if (methodNode.name.equals("onPlayerStoppedUsing")) {
                patchMethodUsingRelativeRotations(methodNode, GET_ROTATIONYAW + GET_ROTATIONPITCH);
                break;
            }
        }

        ClassWriter classWriter = new ClassWriter(0);
        classNode.accept(classWriter);
        return classWriter.toByteArray();
    }

    private static byte[] patchThrowableMojangItem(byte[] bytes) {
        ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(bytes);
        classReader.accept(classNode, 0);

        for (MethodNode methodNode : classNode.methods) {
            if (methodNode.name.equals("onItemRightClick")) {
                patchMethodUsingRelativeRotations(methodNode, GET_ROTATIONYAW + GET_ROTATIONPITCH);
                break;
            }
        }

        ClassWriter classWriter = new ClassWriter(0);
        classNode.accept(classWriter);
        return classWriter.toByteArray();
    }

    private static byte[] patchEntityFishHook(byte[] bytes) {
        ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(bytes);
        classReader.accept(classNode, 0);

        for (MethodNode methodNode : classNode.methods) {
            if (methodNode.name.equals("<init>")
                    && methodNode.desc.equals("(Lnet/minecraft/world/World;Lnet/minecraft/entity/player/EntityPlayer;)V")) {
                patchMethodUsingRelativeRotations(methodNode, GET_ROTATIONYAW + GET_ROTATIONPITCH);
                break;
            }
        }

        ClassWriter classWriter = new ClassWriter(0);
        classNode.accept(classWriter);
        return classWriter.toByteArray();
    }

    private static byte[] patchRenderGlobal(byte[] bytes) {
        ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(bytes);
        classReader.accept(classNode, 0);

        outerfor:
        for (MethodNode methodNode : classNode.methods) {
            if (methodNode.name.equals("getViewVector")) {
                for (ListIterator<AbstractInsnNode> iterator = methodNode.instructions.iterator(); iterator.hasNext(); ) {
                    AbstractInsnNode next = iterator.next();
                    if (next instanceof MethodInsnNode) {
                        MethodInsnNode methodInsnNode = (MethodInsnNode)next;
                        if (methodInsnNode.getOpcode() == Opcodes.INVOKESPECIAL
                                && methodInsnNode.owner.equals("org/lwjgl/util/vector/Vector3f")
                                && methodInsnNode.name.equals("<init>")
                                && methodInsnNode.desc.equals("(FFF)V")) {
                            iterator.add(new VarInsnNode(Opcodes.ALOAD, 1)); // load the passed Entity argument
                            iterator.add(new MethodInsnNode(Opcodes.INVOKESTATIC,
                                    "uk/co/mysterymayhem/gravitymod/asm/Hooks",
                                    "adjustViewVector",
                                    "(Lorg/lwjgl/util/vector/Vector3f;Lnet/minecraft/entity/Entity;)Lorg/lwjgl/util/vector/Vector3f;",
                                    false));
                            break outerfor;
                        }
                    }
                }
            }
        }

        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        classNode.accept(classWriter);
        return classWriter.toByteArray();
    }

//    private static byte[] patchEntityPlayerClass(byte[] bytes) {
//        String getLookMethodName = FMLLoadingPlugin.runtimeDeobfEnabled ? "getVectorForRotation" : "func_174806_f";
//
//        ClassNode classNode = new ClassNode();
//        ClassReader classReader = new ClassReader(bytes);
//        classReader.accept(classNode, 0);
//
//        Iterator<MethodNode> methods = classNode.methods.iterator();
//        while (methods.hasNext()) {
//            MethodNode node = methods.next();
//
//            if (node.)
//        }
//
//        return bytes;
//    }
//
//    private static byte[] patchEntityPlayerSPClass(byte[] bytes) {
//        return bytes;
//    }
}