using MedicalDelivery.Models;
using Microsoft.EntityFrameworkCore;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace MedicalDelivery.Helpers
{
    public class LibraryContext : DbContext
    {
        public LibraryContext(DbContextOptions<LibraryContext> options) : base(options)
        {
        }
        public DbSet<Hospital> hospitals { get; set; }
        public DbSet<Rider> riders { get; set; }
        public DbSet<Medicine> medicines { get; set; }
        public DbSet<Delivery> deliveries { get; set; }
        public DbSet<Update> updates { get; set; }

    }
}
