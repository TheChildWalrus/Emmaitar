package emmaitar.common.core;

import java.util.Iterator;

import net.minecraft.entity.EntityTrackerEntry;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.launchwrapper.IClassTransformer;

import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;

import emmaitar.common.Emmaitar;
import emmaitar.common.EntityCustomPainting;

public class EmmaitarClassTransformer implements IClassTransformer
{
	private static final String cls_EntityPlayerMP = "net/minecraft/entity/player/EntityPlayerMP";
	private static final String cls_EntityPlayerMP_obf = "mw";

	@Override
	public byte[] transform(String name, String transformedName, byte[] basicClass)
	{
		if (name.equals("my") || name.equals("net.minecraft.entity.EntityTrackerEntry"))
		{
			return patchEntityTrackerEntry(name, basicClass);
		}
	
		return basicClass;
	}

	private byte[] patchEntityTrackerEntry(String name, byte[] bytes)
	{
		String targetMethodName = "tryStartWachingThis";
		String targetMethodNameObf = "func_73117_b";
		String targetMethodSign = "(L" + cls_EntityPlayerMP + ";)V";
		String targetMethodSignObf = "(L" + cls_EntityPlayerMP_obf + ";)V";

		ClassNode classNode = new ClassNode();
		ClassReader classReader = new ClassReader(bytes);
		classReader.accept(classNode, 0);
		
		for (MethodNode method : classNode.methods)
		{
			if ((method.name.equals(targetMethodName) || method.name.equals(targetMethodNameObf)) && (method.desc.equals(targetMethodSign) || method.desc.equals(targetMethodSignObf)))
			{
				Iterator<AbstractInsnNode> it = method.instructions.iterator();
				nodeLoop:
				while (it.hasNext())
				{
					AbstractInsnNode node = it.next();
					
					if (node instanceof JumpInsnNode && node.getOpcode() == Opcodes.IF_ACMPEQ)
					{
						JumpInsnNode jumpNode = (JumpInsnNode)node;
						LabelNode label = jumpNode.label;

				        InsnList newIns = new InsnList();
				        newIns.add(new VarInsnNode(Opcodes.ALOAD, 1));
				        newIns.add(new VarInsnNode(Opcodes.ALOAD, 0));
				        newIns.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "emmaitar/common/core/EmmaitarClassTransformer", "ETE_cancel", "(Lnet/minecraft/entity/player/EntityPlayerMP;Lnet/minecraft/entity/EntityTrackerEntry;)Z", false));
				        newIns.add(new JumpInsnNode(Opcodes.IFNE, label));
				        
				        method.instructions.insert(jumpNode, newIns);
				        
				        break nodeLoop;
					}
				}
				
				System.out.println("Emmaitar Core: Patched method " + method.name);
			}
		}
		
		ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
		classNode.accept(writer);
		return writer.toByteArray();
	}
	
	public static boolean ETE_cancel(EntityPlayerMP tracker, EntityTrackerEntry entry)
	{
		if (entry.myEntity instanceof EntityCustomPainting && !Emmaitar.modEventHandler.shouldSendPaintingToClient(tracker))
		{
			//TODO remove this
			System.out.println("Not sending painting because " + tracker.getCommandSenderName() + " does not have mod.");
			return true;
		}
		return false;
	}
}
