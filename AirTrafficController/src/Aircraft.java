
class Aircraft
    {
	private String _id;
        private AircraftSize _size;
        private AircraftType _type;
        private int _enqueueOrder;

        public Aircraft(String ID, AircraftSize size, AircraftType type, int enqueueOrder)
        {
            _id = ID;
            _size = size;
            _type = type;
            _enqueueOrder = enqueueOrder;
        }


        public String getID(){ return _id; }
        public void setID(String value) { _id = value; } 
        
        public AircraftSize getSize(){ return _size; }
        public void setSize(AircraftSize value) { _size = value; }

        public AircraftType getType(){ return _type; }
        public void setType(AircraftType value) { _type = value; }

        public int getOrder(){ return _enqueueOrder; }
        public void setOrder(int value) { _enqueueOrder = value; }
        
        @Override
        public String toString()
        {
            return String.format("ID:%s        Type:%s         Size:%s         Order:%d", _id, _type.toString(), _size.toString(), _enqueueOrder);
        }

    }
