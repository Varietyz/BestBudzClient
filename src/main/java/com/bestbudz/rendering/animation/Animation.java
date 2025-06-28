package com.bestbudz.rendering.animation;

import com.bestbudz.network.ArchiveLoader;
import com.bestbudz.network.Stream;
import com.bestbudz.rendering.SequenceFrame;

public final class Animation {

    public static Animation[] anims;
    public int frameCount;
    public int[] frameIds;
    public int[] anIntArray354;
    public int[] anIntArray355;
    public int loopOffset;
    public int[] anIntArray357;
    public boolean resetOnMove;
    public int anInt359;
    public int anInt360;
    public int anInt361;
    public int maxLoops;
    public int priority;
    public int anInt364;
    public int anInt365;
    private Animation() {
        loopOffset = -1;
        resetOnMove = false;
        anInt359 = 5;
		anInt360 = -1;
		anInt361 = -1;
		maxLoops = 99;
		priority = -1;
		anInt364 = -1;
        anInt365 = 1;
    }

    public static void unpackConfig(ArchiveLoader archiveLoader)
    {
		Stream stream = new Stream(archiveLoader.extractFile("seq.dat"));
        int length = stream.readUnsignedWord();
        System.out.println("Animations Loaded: "+length);
        if(anims == null)
            anims = new Animation[length + 5000];
        for(int j = 0; j < length; j++) {
            if(anims[j] == null)
                anims[j] = new Animation();
            anims[j].readValues(stream);

        }
    }

    public int getFrameDuration(int i) {
        int j = anIntArray355[i];
        if(j == 0)
        {
            SequenceFrame class36 = SequenceFrame.getFrame(frameIds[i]);
            if(class36 != null)
                j = anIntArray355[i] = class36.version;
        }
        if(j == 0)
            j = 1;
        return j;
    }

private void readValues(Stream stream) {
			int i;
			while ((i = stream.readUnsignedByte()) != 0){


			if (i == 1) {
				frameCount = stream.readUnsignedWord();
				frameIds = new int[frameCount];
				anIntArray354 = new int[frameCount];
				anIntArray355 = new int[frameCount];
				for (int j = 0; j < frameCount; j++) {
						frameIds[j] = stream.readDWord();
						anIntArray354[j] = -1;
					}


					for (int j = 0; j < frameCount; j++)
						anIntArray355[j] = stream.readUnsignedByte();

			} else if (i == 2)
				loopOffset = stream.readUnsignedWord();
			else if (i == 3) {
				int k = stream.readUnsignedByte();
				anIntArray357 = new int[k + 1];
				for (int l = 0; l < k; l++)
					anIntArray357[l] = stream.readUnsignedByte();
				anIntArray357[k] = 9999999;
			} else if (i == 4)
				resetOnMove = true;
			else if (i == 5)
				anInt359 = stream.readUnsignedByte();
			else if (i == 6)
				anInt360 = stream.readUnsignedWord();
			else if (i == 7)
				anInt361 = stream.readUnsignedWord();
			else if (i == 8)
				maxLoops = stream.readUnsignedByte();
			else if (i == 9)
				priority = stream.readUnsignedByte();
			else if (i == 10)
				anInt364 = stream.readUnsignedByte();
			else if (i == 11)
				anInt365 = stream.readUnsignedByte();
			else if (i == 12)
				stream.readDWord();
			else
				System.out.println("Error unrecognised seq config code: " + i);
			}
			if (frameCount == 0) {
			frameCount = 1;
			frameIds = new int[1];
			frameIds[0] = -1;
			anIntArray354 = new int[1];
			anIntArray354[0] = -1;
			anIntArray355 = new int[1];
			anIntArray355[0] = -1;
		}
		if (priority == -1)
			if (anIntArray357 != null)
				priority = 2;
			else
				priority = 0;
		if (anInt364 == -1) {
			if (anIntArray357 != null) {
				anInt364 = 2;
				return;
			}
			anInt364 = 0;
		}
	}
}
