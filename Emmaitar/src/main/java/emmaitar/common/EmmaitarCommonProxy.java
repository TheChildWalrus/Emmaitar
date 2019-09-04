package emmaitar.common;

public class EmmaitarCommonProxy
{
	public void load()
	{
	}

	public CustomPaintingData lookupClientPainting(CustomPaintingReference reference)
	{
		return null;
	}

	public CustomPaintingData lookupPaintingByDyes(DyeReference[] dyes)
	{
		return PaintingCatalogue.lookupByDyes(dyes);
	}
}
