package emmaitar.common.core;

import java.util.Map;

import cpw.mods.fml.relauncher.*;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin.MCVersion;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin.SortingIndex;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin.TransformerExclusions;

@TransformerExclusions({"emmaitar"})
@SortingIndex(1001)
@MCVersion("1.7.10")
public class EmmaitarLoadingPlugin implements IFMLLoadingPlugin
{
	@Override
	public String[] getASMTransformerClass()
	{
		return new String[] {EmmaitarClassTransformer.class.getName()};
	}

	@Override
	public String getModContainerClass()
	{
		return null;
	}

	@Override
	public String getSetupClass()
	{
		return null;
	}

	@Override
	public void injectData(Map<String, Object> data) {}

	@Override
	public String getAccessTransformerClass()
	{
		return null;
	}
}
