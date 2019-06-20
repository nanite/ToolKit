function initializeCoreMod() {
     return {
         'StructureBlockTileEntity Transformer': {
             'target': {
                 'type': 'CLASS',
                 'name': 'net.minecraft.tileentity.StructureBlockTileEntity'

             },
             'transformer': function(classNode) {
                 var opcodes = Java.type('org.objectweb.asm.Opcodes')
                 var methods = classNode.methods;
                 var targetMethodName = "read";

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
                 return classNode;
             }
         }
     }
 }