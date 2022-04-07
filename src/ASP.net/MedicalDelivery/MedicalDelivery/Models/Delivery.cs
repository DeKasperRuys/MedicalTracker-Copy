using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace MedicalDelivery.Models
{
    public class Delivery
    {
        public int ID { get; set; }
        public int status { get; set; }
        public virtual Medicine medicine { get; set; }
        public int amount { get; set; }
        public Delivery delivery { get; set; }
        public virtual Hospital hospital { get; set; }
        public virtual Rider rider { get; set; }
        public bool notification { get; set; }
        public bool doctorNotification { get; set; }
        public bool riderNotification { get; set; }
        public bool response { get; set; }

    }
}
