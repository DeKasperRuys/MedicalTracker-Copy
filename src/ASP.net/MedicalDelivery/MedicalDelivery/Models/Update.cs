﻿using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace MedicalDelivery.Models
{
    public class Update
    {
        public int ID { get; set; }
        public Delivery delivery { get; set; }
        public DateTime timeStamp { get; set; }
        public float lat { get; set; }
        public float lon { get; set; }
        public double temp { get; set; }
        public double humid { get; set; }
        public bool movement { get; set; }
        public bool orientation { get; set; }
    }
}
