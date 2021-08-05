package distributedSystem.DCMSCorba;


/**
* distributedSystem/DCMSCorba/DCMSInterfaceHelper.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from java.idl
* Wednesday, 4 August, 2021 7:57:16 PM EDT
*/

abstract public class DCMSInterfaceHelper
{
  private static String  _id = "IDL:distributedSystem/DCMSCorba/DCMSInterface:1.0";

  public static void insert (org.omg.CORBA.Any a, distributedSystem.DCMSCorba.DCMSInterface that)
  {
    org.omg.CORBA.portable.OutputStream out = a.create_output_stream ();
    a.type (type ());
    write (out, that);
    a.read_value (out.create_input_stream (), type ());
  }

  public static distributedSystem.DCMSCorba.DCMSInterface extract (org.omg.CORBA.Any a)
  {
    return read (a.create_input_stream ());
  }

  private static org.omg.CORBA.TypeCode __typeCode = null;
  synchronized public static org.omg.CORBA.TypeCode type ()
  {
    if (__typeCode == null)
    {
      __typeCode = org.omg.CORBA.ORB.init ().create_interface_tc (distributedSystem.DCMSCorba.DCMSInterfaceHelper.id (), "DCMSInterface");
    }
    return __typeCode;
  }

  public static String id ()
  {
    return _id;
  }

  public static distributedSystem.DCMSCorba.DCMSInterface read (org.omg.CORBA.portable.InputStream istream)
  {
    return narrow (istream.read_Object (_DCMSInterfaceStub.class));
  }

  public static void write (org.omg.CORBA.portable.OutputStream ostream, distributedSystem.DCMSCorba.DCMSInterface value)
  {
    ostream.write_Object ((org.omg.CORBA.Object) value);
  }

  public static distributedSystem.DCMSCorba.DCMSInterface narrow (org.omg.CORBA.Object obj)
  {
    if (obj == null)
      return null;
    else if (obj instanceof distributedSystem.DCMSCorba.DCMSInterface)
      return (distributedSystem.DCMSCorba.DCMSInterface)obj;
    else if (!obj._is_a (id ()))
      throw new org.omg.CORBA.BAD_PARAM ();
    else
    {
      org.omg.CORBA.portable.Delegate delegate = ((org.omg.CORBA.portable.ObjectImpl)obj)._get_delegate ();
      distributedSystem.DCMSCorba._DCMSInterfaceStub stub = new distributedSystem.DCMSCorba._DCMSInterfaceStub ();
      stub._set_delegate(delegate);
      return stub;
    }
  }

  public static distributedSystem.DCMSCorba.DCMSInterface unchecked_narrow (org.omg.CORBA.Object obj)
  {
    if (obj == null)
      return null;
    else if (obj instanceof distributedSystem.DCMSCorba.DCMSInterface)
      return (distributedSystem.DCMSCorba.DCMSInterface)obj;
    else
    {
      org.omg.CORBA.portable.Delegate delegate = ((org.omg.CORBA.portable.ObjectImpl)obj)._get_delegate ();
      distributedSystem.DCMSCorba._DCMSInterfaceStub stub = new distributedSystem.DCMSCorba._DCMSInterfaceStub ();
      stub._set_delegate(delegate);
      return stub;
    }
  }

}