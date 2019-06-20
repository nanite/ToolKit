function initializeCoreMod() {
     return {
         'StructureBlockTileEntity Transformer': {
             'target': {
                 'type': 'CLASS',
                 'name': 'net.minecraft.tileentity.StructureBlockTileEntity'

             },
             'transformer': function(classNode) {
                 var opcodes = Java.type('org.objectweb.asm.Opcodes');
                 var MethodNode = Java.type('org.objectweb.asm.tree.MethodNode');
                 var ASMAPI = Java.type('net.minecraftforge.coremod.api.ASMAPI');
                 var methods = classNode.methods;
                 var targetMethodName = ASMAPI.mapMethod("func_145839_a");

                 for (var j in methods) {
                 var method = methods[j];
                     if(method.name.equals(targetMethodName)){
                         var insnarray = method.instructions.toArray()
                         for (var insnid in insnarray)
                         {
                             var insn = insnarray[insnid]
                             if (insn instanceof Java.type("org.objectweb.asm.tree.IntInsnNode"))
                             {
                                 if (insn.operand == 32)
                                 {
                                     if (insn.getPrevious() instanceof Java.type("org.objectweb.asm.tree.IntInsnNode"))
                                         {
                                         continue
                                         }
                                     insn.setOpcode(opcodes.SIPUSH)
                                     insn.operand = 512
                                 }
                             }
                         }
                     }
                 }

                 var newMethod = new MethodNode(opcodes.ACC_PUBLIC, ASMAPI.mapMethod("func_145833_n"), "()D", null, null);
                 newMethod.visitLdcInsn(16384.0);
                 newMethod.visitInsn(opcodes.DRETURN);
                 methods.add(newMethod);

                 return classNode;
             }
         }
     }
 }