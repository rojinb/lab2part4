package arch.sm213.machine.student;

import static org.junit.Assert.*;
import machine.AbstractMainMemory.InvalidAddressException;

import org.junit.Before;
import org.junit.Test;

public class MainMemoryTest {
	private MainMemory memory;
	
	@Before
	public void beforeEachTest(){
		memory = new MainMemory(0xf);
	}
	
	/*testing whether isAccessAligned returns correct value given an address and length*/
	@Test
	public void testIsAccessAligned(){
		assertTrue(memory.isAccessAligned(0x4,4)); //4 mod 4 == 0 testing when address is same as length
		assertTrue(memory.isAccessAligned(0x20,8)); //testing an address bigger than the length
		assertFalse(memory.isAccessAligned(0xff, 16)); //testing an address stored as negative value in signed (false case)
		assertTrue(memory.isAccessAligned(0xffffb0, 16)); //testing an address stored as negative value in signed (true case)
		assertTrue(memory.isAccessAligned(0xaaaaaaaa, 2)); 
		assertFalse(memory.isAccessAligned(0xabcd, 16));
	}
	
	@Test
	public void testBytesToInteger(){
	
		assertTrue(-1 == memory.bytesToInteger((byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff));//testing a negative int
		assertTrue(0 == memory.bytesToInteger((byte) 0x0, (byte) 0x0, (byte) 0x0, (byte) 0x0));//testing a zero int
		assertTrue(1 == memory.bytesToInteger((byte) 0x0, (byte) 0x0, (byte) 0x0, (byte) 0x1));//testing 1
		assertTrue(50989836 == memory.bytesToInteger((byte) 0x3, (byte) 0xa, (byte) 0xb, (byte) 0xc));//testing a big positive int
	}
	
	@Test
	public void testIntegerToBytes(){
		byte[] b = memory.integerToBytes((int) 0x1020304); //testing a simple int
		assertTrue(b[0] == 0x1);
		assertTrue(b[1] == 0x2);
		assertTrue(b[2] == 0x3);
		assertTrue(b[3] == 0x4);
		
		
		b = memory.integerToBytes((int) 0xa0b0c0d); //testing a positive int
		assertTrue(b[0] == 0xa);
		assertTrue(b[1] == 0x0b);
		assertTrue(b[2] == 0x0c);
		assertTrue(b[3] == 0x0d);
		
		b = memory.integerToBytes((int) 0xaabbccdd); //testing a negative int
		assertTrue(b[0] == (byte) 0xaa);
		assertTrue(b[1] == (byte) 0xbb);
		assertTrue(b[2] == (byte) 0xcc);
		assertTrue(b[3] == (byte) 0xdd);
		
		b = memory.integerToBytes((int) 0xffffffff); //testing a negative int
		assertTrue(b[0] == (byte) 0xff);
		assertTrue(b[1] == (byte) 0xff);
		assertTrue(b[2] == (byte) 0xff);
		assertTrue(b[3] == (byte) 0xff);

		b = memory.integerToBytes((int) 0x0); //testing a zero int
		assertTrue(b[0] == (byte) 0x0);
		assertTrue(b[1] == (byte) 0x0);
		assertTrue(b[2] == (byte) 0x0);
		assertTrue(b[3] == (byte) 0x0);
		
		b = memory.integerToBytes((int) 0x1); //testing a  int == 1
		assertTrue(b[0] == (byte) 0x0);
		assertTrue(b[1] == (byte) 0x0);
		assertTrue(b[2] == (byte) 0x0);
		assertTrue(b[3] == (byte) 0x1);	
	}
	
	@Test
	public void testSetGetValid(){ //testing get(int,int) with a VALID address
		try{
			//testing set/get with valid address and 4 byte of values (beginning at address 0)
			byte[] vals = {0x4, 0xa, 0xd, 0x20};
			memory.set(0x0, vals);

			byte[] b = memory.get(0x0,4);
			
			assertTrue(vals[0] == b[0]);
			assertTrue(vals[1] == b[1]);
			assertTrue(vals[2] == b[2]);
			assertTrue(vals[3] == b[3]);
			
			//testing set/get with valid address and 8 byte of values (beginning at nonzero address)
			//also set overrides some of previus values
			byte[] vals2 = {0xf, 0x10, 0x3, 0x5e, 0x9, 0x9, 0xa, 0xb};
			memory.set(0x4, vals2);
			b = memory.get(0x4, 8);
			
			assertTrue(vals2[0] == b[0]);
			assertTrue(vals2[1] == b[1]);
			assertTrue(vals2[2] == b[2]);
			assertTrue(vals2[3] == b[3]);
			assertTrue(vals2[4] == b[4]);
			assertTrue(vals2[5] == b[5]);
			assertTrue(vals2[6] == b[6]);
			assertTrue(vals2[7] == b[7]);
			
		}catch(InvalidAddressException e){
			fail("the address wasnt invalid");
		}
	}
	
	//testing Set() with invalid address at first address
	@Test (expected = InvalidAddressException.class)
	public void testSetInvalidAtGivenAddress() throws InvalidAddressException{
		byte[] values = {0x5, 0x7};
		memory.set(0xff,values );
	}
	
	//testing Set() with invalid address for non first address
		@Test (expected = InvalidAddressException.class)
		public void testSetInvalidAtAnAddress() throws InvalidAddressException{
			byte[] values = {0x5, 0x7, 0x8, 0xe, 0x3, 0xa, 0x10, 0xd};
			memory.set(0xa,values);
	}
		
		//testing get() with invalid address at first address
		@Test (expected = InvalidAddressException.class)
		public void testgetInvalidAtGivenAddress() throws InvalidAddressException{
			
			memory.get(0xff, 2);
		}
		
		//testing get() with invalid address for non first address
			@Test (expected = InvalidAddressException.class)
			public void testgetInvalidAtAnAddress() throws InvalidAddressException{
				
				memory.get(0xa, 5);
			}	
}
