package org.jgroups.conf;

import java.util.Properties;
import java.util.List;
import java.util.concurrent.Callable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;

import org.jgroups.View;
import org.jgroups.util.Util;
import org.jgroups.stack.Protocol ;
import org.jgroups.stack.IpAddress ;
import org.jgroups.stack.Configurator ;

/**
 * Groups a set of standard PropertyConverter(s) supplied by JGroups.
 * 
 * <p>
 * Third parties can provide their own converters if such need arises by
 * implementing {@link PropertyConverter} interface and by specifying that
 * converter as converter on a specific Property annotation of a field or a
 * method instance.
 * 
 * @author Vladimir Blagojevic
 * @version $Id: PropertyConverters.java,v 1.9 2009/09/30 17:28:43 rachmatowicz Exp $
 */
public class PropertyConverters {

    public static class NetworkInterfaceList implements PropertyConverter {

        public Object convert(Protocol protocol, Class<?> propertyFieldType, Properties props, String propertyValue) throws Exception {
            return Util.parseInterfaceList(propertyValue);
        }

        public String toString(Object value) {
            List<NetworkInterface> list=(List<NetworkInterface>)value;
            StringBuilder sb=new StringBuilder();
            boolean first=true;
            for(NetworkInterface intf: list) {
                if(first)
                    first=false;
                else
                    sb.append(",");
                sb.append(intf.getName());
            }
            return sb.toString();
        }
    }
    
    public static class FlushInvoker implements PropertyConverter{

		public Object convert(Protocol protocol, Class<?> propertyFieldType, Properties props,
				String propertyValue) throws Exception {
			if (propertyValue == null) {
				return null;
			} else {
				Class<Callable<Boolean>> invoker = (Class<Callable<Boolean>>) Class.forName(propertyValue);
				invoker.getDeclaredConstructor(View.class);
				return invoker;
			}
		}

		public String toString(Object value) {
			return value.getClass().getName();
		}
    	
    }

    public static class InitialHosts implements PropertyConverter{
    	
		public Object convert(Protocol protocol, Class<?> propertyFieldType, Properties props, String propertyValue) throws Exception {
			int port_range = getPortRange(protocol) ;
			List<IpAddress> addresses = Util.parseCommaDelimitedHosts(propertyValue, port_range) ;	
			
			// debugging
			System.out.println("PropertyConverter.InitialHosts:");
			System.out.println("port_range="+port_range) ;
	    	for(IpAddress addr: addresses) {
	    		System.out.println("host: " + addr.toString()) ;
	    	}
	    	
			return addresses ;
		}

		public String toString(Object value) {
			return value.getClass().getName();
		}
		
		private int getPortRange(Protocol protocol) {
			int port_range = 0 ;	
			// get the field value
			try {
				Field f = protocol.getClass().getDeclaredField("port_range") ;
				port_range = ((Integer) Configurator.getField(f,protocol)).intValue() ;
			}
			catch(NoSuchFieldException e) {	
				System.out.println("InitialHosts: no such field: port_range");
			}
			return port_range ;
		}
    }
    
    public static class InitialHosts2 implements PropertyConverter {
    	
		public Object convert(Protocol protocol, Class<?> propertyFieldType, Properties props, String propertyValue) throws Exception {
			// port range is 1
			List<InetSocketAddress> addresses = Util.parseCommaDelimetedHosts2(propertyValue, 1) ;			
			return addresses ;
		}

		public String toString(Object value) {
			return value.getClass().getName();
		}		
    }
    
    public static class BindAddress implements PropertyConverter {

        public Object convert(Protocol protocol, Class<?> propertyFieldType, Properties props, String propertyValue) throws Exception {
            return Util.getBindAddress(props);
        }

        public String toString(Object value) {
            InetAddress val=(InetAddress)value;
            return val.getHostAddress();
        }
    }
    
    public static class LongArray implements PropertyConverter {

        public Object convert(Protocol protocol, Class<?> propertyFieldType, Properties props, String propertyValue) throws Exception {
            long tmp [] = Util.parseCommaDelimitedLongs(propertyValue);
            if(tmp != null && tmp.length > 0){
                return tmp;
            }else{
                // throw new Exception ("Invalid long array specified in " + propertyValue);
                return null;
            }
        }

        public String toString(Object value) {
            if(value == null)
                return null;
            long[] val=(long[])value;
            StringBuilder sb=new StringBuilder();
            boolean first=true;
            for(long l: val) {
                if(first)
                    first=false;
                else
                    sb.append(",");
                sb.append(l);
            }
            return sb.toString();
        }
    }


    public static class Default implements PropertyConverter {
        public Object convert(Protocol protocol, Class<?> propertyFieldType, Properties props, String propertyValue) throws Exception {
            if(propertyValue == null)
                throw new NullPointerException("Property value cannot be null");
            if(Boolean.TYPE.equals(propertyFieldType)) {
                return Boolean.parseBoolean(propertyValue);
            }
            else if(Integer.TYPE.equals(propertyFieldType)) {
                return Integer.parseInt(propertyValue);
            }
            else if(Long.TYPE.equals(propertyFieldType)) {
                return Long.parseLong(propertyValue);
            }
            else if(Byte.TYPE.equals(propertyFieldType)) {
                return Byte.parseByte(propertyValue);
            }
            else if(Double.TYPE.equals(propertyFieldType)) {
                return Double.parseDouble(propertyValue);
            }
            else if(Short.TYPE.equals(propertyFieldType)) {
                return Short.parseShort(propertyValue);
            }
            else if(Float.TYPE.equals(propertyFieldType)) {
                return Float.parseFloat(propertyValue);
            }
            return propertyValue;
        }

        public String toString(Object value) {
            return value != null? value.toString() : null;
        }
    }
}