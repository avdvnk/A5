package algorithm;


public class KeyGenerator {
	private static final int KEYLENGTH = 64;
	private static final int FRAMENUMBERLENGTH = 22;
	private static final int FRAMELENGTH = 228;
	private long key;
	private int frameNumber;
	private byte[] r1 = new byte[19]; 
	private byte[] r2 = new byte[22];
	private byte[] r3 = new byte[23];
	private static final byte[] R1TAPS = {18, 17, 16, 13};
	private static final byte[] R2TAPS = {21, 20};
	private static final byte[] R3TAPS = {22, 21, 20, 7};
	private static final byte R1CLK = 8;
	private static final byte R2CLK = 10;
	private static final byte R3CLK = 10;
	private int bitCount = 0;
	
	public KeyGenerator(byte[] key, byte[] frameNumber) {
		this.key = ByteUtils.bytesToLong(key);
		String value = Integer.toBinaryString(ByteUtils.bytesToInt(frameNumber));
		this.frameNumber = Integer.parseInt(value.substring(9), 2);
	}

	public void initPeriod() {
		mixKey();
		mixFrameNumber();
		for(int i = 0; i < 100; i++) {
			regularCycle();
		}
	}

	private void incrementFrameNumber() {
		this.frameNumber++;
	}

	public byte getStreamKey() {
		byte streamByte;
		StringBuilder sb = new StringBuilder();
		for(int j = 0; j < 8; j++) {
			if( this.bitCount == FRAMELENGTH ) {
				this.bitCount = 0;
				initPeriod();
				this.incrementFrameNumber();
			}
			byte regOut = (byte) ((r1[18] + r2[21] + r3[22]) % 2);
			sb.append(regOut);
			regularCycle();
			this.bitCount++;
		}
		streamByte = (byte) Integer.parseInt(sb.toString(), 2);
		return streamByte;
	}

	private void mixKey() {
		int count = 0;
		byte result, lsbKey;
		while (count < KEYLENGTH) {
			lsbKey = (byte)(key >>> count & 0x1);
			result = (byte) ((lsbKey + clockReg(1)) % 2);
			shiftReg(1, result);

			result = (byte) ((lsbKey + clockReg(2)) % 2);
			shiftReg(2, result);

			result = (byte) ((lsbKey + clockReg(3)) % 2);
			shiftReg(3, result);
			count++;
		}
	}

	private void mixFrameNumber() {
		int count = 0;
		byte result, lsbKey;
		while (count < FRAMENUMBERLENGTH) {
			lsbKey = (byte)(frameNumber >>> count & 0x1);
			result = (byte) ((lsbKey + clockReg(1)) % 2);
			shiftReg(1, result);
			result = (byte) ((lsbKey + clockReg(2)) % 2);
			shiftReg(2, result);
			result = (byte) ((lsbKey + clockReg(3)) % 2);
			shiftReg(3, result);
			count++;
		}
	}

	private void regularCycle() {
		byte val1, val2, val3;
		byte vote = majorityVote();
		if(r1[R1CLK] == vote) {
			val1 = clockReg(1);
			shiftReg(1, val1);
		}
		if(r2[R2CLK] == vote) {
			val2 = clockReg(2);
			shiftReg(2, val2);
		}
		if(r3[R3CLK] == vote) {
			val3 = clockReg(3);
			shiftReg(3, val3);
		}
	}

	private byte majorityVote() {
		return (byte)(r1[R1CLK] & r2[R2CLK] | r1[R1CLK] & r3[R3CLK] | r2[R2CLK]);
	}

	private byte clockReg(int register) {
		byte output = 0;
		switch(register) {
			case 1:
				for(byte tap : R1TAPS) {
					output += r1[tap];
				}
				output = (byte) (output % 2);
				break;
			case 2:
				for(byte tap : R2TAPS) {
					output += r2[tap];
				}
				output = (byte) (output % 2);
				break;
			case 3:
				for(byte tap : R3TAPS) {
					output += r3[tap];
				}
				output = (byte) (output % 2);
				break;
			default:
				throw new IllegalArgumentException("Invalid Register Value");
		}
		return output;
	}

	private void shiftReg(int register, byte input) {
		switch(register) {
			case 1:
				System.arraycopy(r1, 0, r1, 1, r1.length - 1);
				r1[0] = input;
				break;
			case 2:
				System.arraycopy(r2, 0, r2, 1, r2.length - 1);
				r2[0] = input;
				break;
			case 3:
				System.arraycopy(r3, 0, r3, 1, r3.length - 1);
				r3[0] = input;
				break;
			default:
				throw new IllegalArgumentException("Invalid Register Value");
		}
	}
}
