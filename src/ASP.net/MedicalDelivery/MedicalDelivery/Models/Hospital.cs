using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace MedicalDelivery.Models
{
    public class Hospital
    {
        public int ID { get; set; }
        public string name { get; set; }
        public Double lat { get; set; }
        public Double lon { get; set; }
        public string address { get; set; }
    }
}
