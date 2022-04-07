using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace MedicalDelivery.Models
{
    public class Medicine
    {
        public int ID { get; set; }
        public string name { get; set; }
        public double minTemp { get; set; }
        public double maxTemp { get; set; }
        public double minHumid { get; set; }
        public double maxHumid { get; set; }
        public bool movement { get; set; }
        public bool orientation { get; set; }
    }
}
